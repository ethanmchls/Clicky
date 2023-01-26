package com.ethanmichaelis.clicky

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ethanmichaelis.clicky.database.DatabaseManager
import com.ethanmichaelis.clicky.nav.AuthType
import com.ethanmichaelis.clicky.nav.AuthType.LOGIN
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class ClickyViewModel @Inject constructor(
    app: Application,
    private val clickyStore: ClickyStore,
    private val databaseManager: DatabaseManager,
) : AndroidViewModel(app) {

    fun handleDeepLinks(intent: Intent, onSuccess: (UserSession) -> Unit) =
        databaseManager.handleDeeplinks(intent, onSuccess)

    private val _initialized = MutableStateFlow(false)
    val initialized: StateFlow<Boolean> = _initialized

    private val _loggedIn = MutableStateFlow(false)
    val loggedIn: StateFlow<Boolean> = _loggedIn
    private val _signupRequested = MutableStateFlow(false)
    val signupRequested: StateFlow<Boolean> = _signupRequested

    private val _score = MutableStateFlow(0L)
    val score: StateFlow<Long> = _score

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _rank = MutableStateFlow(999)
    val rank: StateFlow<Int> = _rank

    private val _topTen = MutableStateFlow(LinkedList<Pair<String, Long>>())
    val topTen: StateFlow<LinkedList<Pair<String, Long>>> = _topTen

    private val _usernameError = MutableStateFlow(false)
    val usernameError: StateFlow<Boolean> = _usernameError

    suspend fun init() {
        val isLoggedIn = clickyStore.getLoggedIn().first()
        _loggedIn.emit(isLoggedIn)
        val isSignupRequested = clickyStore.getSignupRequested().first()
        _signupRequested.emit(isSignupRequested)
        if (isLoggedIn) {
            val localScore = clickyStore.getScore().first()
            val remoteScore = try { databaseManager.getClicks() } catch (e: Throwable) { 0L }
            Timber.d("Scores:\nLocal: $localScore\nRemote: $remoteScore")
            val max = listOf(localScore, remoteScore).max()
            Timber.d("Max: $max")
            _score.emit(max)
            val localUsername = clickyStore.getUsername().first()
            // TODO: error if local and remote username are different
            _username.emit(localUsername)
        }
        _initialized.emit(true)
        Timber.d("Finished initializing")
    }

    val sessionFlow = databaseManager.getSessionStatus()

    fun updateScore(newScore: Long) {
        viewModelScope.tryLaunch({
            val currScore = clickyStore.getScore().first()
            if (newScore > currScore) {
                Timber.v("Updating Score:\nCurrent Score $currScore\nNew Score $newScore")
                clickyStore.updateScore(newScore)
                databaseManager.upsertClicks(newScore)
                _score.emit(newScore)
//                databaseManager.getTopTen()
//                databaseManager.getRank(databaseManager.getClicks())
            }
        }, {
            Timber.e(it)
        })
    }

    fun updateUsername(username: String) {
        viewModelScope.tryLaunch({
            clickyStore.updateUsername(username)
            databaseManager.upsertUsername(username)
            _username.emit(username)
        }, {
            Timber.e(it)
        })
    }

    fun login(authType: AuthType, email: String, password: String) {
        viewModelScope.launch {
            if (authType == LOGIN) databaseManager.loginWithEmail(email, password)
            else {
                clickyStore.requestSignup()
                databaseManager.signupWithEmail(email, password)
                _signupRequested.emit(true)
            }
        }
    }

    fun getRank() {
        viewModelScope.launch {
            _rank.emit(databaseManager.getRank(databaseManager.getClicks()))
        }
    }

    fun getTopTen() {
        viewModelScope.launch {
            val topTen = LinkedList<Pair<String, Long>>()
            var position = 0
            databaseManager.getTopTen().forEach {
                position++
                topTen.add(Pair(databaseManager.getUsernameFromId(it.key), it.value))
//                topTen.add(Pair(it.key.take(8), it.value))
                Timber.d("Username: ${databaseManager.getUsernameFromId(it.key)}, Score: ${it.value}")
//                Timber.d("Username: ${it.key.take(8)}, Score: ${it.value}")
            }
            _topTen.emit(topTen)
        }
    }

    suspend fun sessionLogin(session: UserSession) {
        clickyStore.login(session)
        _loggedIn.emit(true)
        val localClicks = clickyStore.getScore().first()
        val remoteClicks = databaseManager.getClicks()
        if (localClicks < remoteClicks) {
            clickyStore.updateScore(remoteClicks)
            _score.emit(remoteClicks)
        }
        try {
            _username.emit(databaseManager.getUsername())
        } catch (_: DatabaseManager.ClickyNoUsernameException) {
            _usernameError.emit(true)
        }
    }

    fun logout() {
        viewModelScope.launch {
            databaseManager.logout()
            clickyStore.logout()
            _loggedIn.emit(false)
            _signupRequested.emit(false)
        }
    }

    private fun CoroutineScope.tryLaunch(
        block: suspend CoroutineScope.() -> Unit,
        onError: (Throwable) -> Unit,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): Job {
        val handler = CoroutineExceptionHandler { _, error ->
            onError(error)
        }
        return launch(start = start, context = handler, block = block)
    }
}
