package com.ethanmichaelis.clicky

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class ClickyStore @Inject constructor(
    private val context: Context
) {
    private val Context.dataStore by preferencesDataStore(
        name = CLICKY_STORE
    )

    fun getScore(): Flow<Long> {
        return context.dataStore.data.map { prefs -> prefs[GLOBAL_SCORE] ?: 0L }
    }

    suspend fun updateScore(newScore: Long) {
        context.dataStore.edit { prefs -> prefs[GLOBAL_SCORE] = newScore }
    }

    fun getUsername(): Flow<String> {
        return context.dataStore.data.map { prefs -> prefs[USERNAME] ?: "" }
    }

    suspend fun updateUsername(username: String) {
        context.dataStore.edit { prefs -> prefs[USERNAME] = username }
    }

    fun getLoggedIn() = context.dataStore.data.map { prefs -> prefs[LOGGED_IN] ?: false }

    fun getSignupRequested() = context.dataStore.data.map { prefs -> prefs[SIGNUP_REQUESTED] ?: false }

    /**
     * Called when signing up. For waiting on the email confirmation.
     */
    suspend fun requestSignup() {
        context.dataStore.edit { prefs ->
            prefs[SIGNUP_REQUESTED] = true
        }
    }

    suspend fun login(session: UserSession) {
        Timber.d(session.toString())
        context.dataStore.edit { prefs ->
            prefs[LOGGED_IN] = true
            prefs[ACCESS_TOKEN] = session.accessToken
            prefs[REFRESH_TOKEN] = session.refreshToken
            prefs[EXPIRES_IN] = session.expiresIn
            prefs[SIGNUP_REQUESTED] = false
        }
    }
    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs[GLOBAL_SCORE] = 0L
            prefs[USERNAME] = ""
            prefs[LOGGED_IN] = false
            prefs[ACCESS_TOKEN] = ""
            prefs[REFRESH_TOKEN] = ""
            prefs[EXPIRES_IN] = 0L
            prefs[SIGNUP_REQUESTED] = false
        }
    }

    companion object {
        private val GLOBAL_SCORE = longPreferencesKey("global_score")
        private val USERNAME = stringPreferencesKey("username")
        private val LOGGED_IN = booleanPreferencesKey("logged_in")
        private val SIGNUP_REQUESTED = booleanPreferencesKey("signup_requested")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val EXPIRES_IN = longPreferencesKey("expires_in")
        private const val CLICKY_STORE = "store"
        const val DEBOUNCE_INTERVAL: Long = 5L * 1000L
    }
}
