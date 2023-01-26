package com.ethanmichaelis.clicky.database

import android.content.Intent
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.*
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber
import javax.inject.Inject

class DatabaseManager @Inject constructor() {
    private val client = createSupabaseClient(
        supabaseUrl = "https://rpexrsthgamnjimcdase.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJwZXhyc3RoZ2FtbmppbWNkYXNlIiwicm9sZSI6ImFub24iLCJpYXQiOjE2NzAwMjExNzcsImV4cCI6MTk4NTU5NzE3N30.ofBXRFBIMo1z_V-4auh1tv4goyTPA5NjDXOF-tv96SU",
    ) {
        install(GoTrue) {
            scheme = "clicky"
            host = "login"
        }
        install(Postgrest)
    }

    fun handleDeeplinks(intent: Intent, onSuccess: (UserSession) -> Unit = {}) =
        client.handleDeeplinks(intent = intent, onSessionSuccess = onSuccess)

    suspend fun signupWithEmail(email: String, password: String) {
        // response ID is user ID
        val response = client.gotrue.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        Timber.d("User ID: ${response.id}")
    }

    suspend fun loginWithEmail(email: String, password: String) {
        client.gotrue.loginWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun logout() {
        client.gotrue.invalidateSession()
    }

    fun getSessionStatus() = client.gotrue.sessionStatus
    suspend fun startAutoRefresh() { client.gotrue.startAutoRefreshForCurrentSession() }

    suspend fun signupWithGoogle() {
        client.gotrue.signUpWith(Google)
    }

    suspend fun loginWithGoogle() {
        client.gotrue.loginWith(Google)
    }

    @Serializable
    data class ClicksEntity(@SerialName("user_id") val userId: String, val clicks: Long)

    suspend fun getClicks(): Long = withUser {
        val clicks = client.postgrest["clicks"].select {
            ClicksEntity::userId eq it.id
        }.decodeSingleOrNull<ClicksEntity>()?.clicks ?: 0L
        Timber.d("Remote Clicks: $clicks")
        clicks
    }

    suspend fun upsertClicks(newScore: Long) = withUser {
        client.postgrest["clicks"].insert(
            value = ClicksEntity(it.id, newScore),
            upsert = true,
        )
    }

    @Throws(ClickyNotAuthenticatedException::class)
    suspend fun getTopTen(): HashMap<String, Long> = withUser {
        val topTen = client.postgrest["clicks"].select {
            order(column = "clicks", order = Order.DESCENDING)
            limit(count = 10L)
        }.decodeList<ClicksEntity>()
        val topTenMap = HashMap<String, Long>()
        topTen.forEach{
            topTenMap[it.userId] = it.clicks
//            Timber.d("User ID: ${it.userId}, Clicks: ${it.clicks}")
        }
        topTenMap
    }

    @Throws(ClickyNotAuthenticatedException::class)
    suspend fun getRank(score: Long): Int = withUser {
        val rank = client.postgrest["clicks"].select {
            ClicksEntity::clicks gt score
        }.decodeList<ClicksEntity>()
        Timber.d("Rank: ${rank.size + 1}")
        rank.size + 1
    }

    @Throws(ClickyNotAuthenticatedException::class)
    suspend fun getUsernameFromId(id: String): String = withUser {
        val username = client.postgrest["usernames"].select {
            UsernameEntity::userId eq id
        }.decodeSingleOrNull<UsernameEntity>()?.username ?: "NO_USERNAME"
        username
    }

    @Serializable
    data class UsernameEntity(@SerialName("user_id") val userId: String, val username: String)
    class ClickyNoUsernameException : Exception("User has not set a username")

    @Throws(ClickyNoUsernameException::class, ClickyNotAuthenticatedException::class)
    suspend fun getUsername(): String = withUser {
        val username = client.postgrest["usernames"].select {
            UsernameEntity::userId eq it.id
        }.decodeSingleOrNull<UsernameEntity>()?.username ?: throw ClickyNoUsernameException()
        Timber.d("Username: $username")
        username
    }

    suspend fun upsertUsername(username: String) = withUser {
        client.postgrest["usernames"].insert(
            value = UsernameEntity(it.id, username),
            upsert = true,
        )
    }

    class ClickyNotAuthenticatedException: Exception("User no longer authenticated")
    @Throws(ClickyNotAuthenticatedException::class)
    private suspend fun <R> withAuth(block: suspend (UserSession, UserInfo) -> R): R {
        val session = (getSessionStatus().first() as? SessionStatus.Authenticated)?.session
            ?: throw ClickyNotAuthenticatedException()
        val user = session.user ?: throw ClickyNotAuthenticatedException()
        client.gotrue.startAutoRefreshForCurrentSession()
        return block.invoke(session, user)
    }
    private suspend fun <R> withSession(block: suspend (UserSession) -> R): R = withAuth { session, _ -> block.invoke(session) }
    private suspend fun <R> withUser(block: suspend (UserInfo) -> R): R = withAuth { _, user -> block.invoke(user) }
}