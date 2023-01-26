package com.ethanmichaelis.clicky.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

interface ClickyDestination {
    val icon: ImageVector
    val route: String
}

object Login : ClickyDestination {
    override val icon = Icons.Filled.AccountCircle//todo
    override val route = "login"
}

object Auth : ClickyDestination {
    override val icon = Icons.Filled.AccountCircle//todo
    override val route = "auth/{type}"
    fun path(authType: AuthType) = "auth/$authType"
}

object Username : ClickyDestination {
    override val icon = Icons.Filled.AccountCircle
    override val route = "username/{login}"
    fun path(login: Boolean) = "username/$login"
}

object Menu : ClickyDestination {
    override val icon = Icons.Filled.Settings
    override val route = "menu"
}

object Clicky : ClickyDestination {
    override val icon = Icons.Filled.AddCircle
    override val route = "clicky"
}

object Leaderboard: ClickyDestination {
    override val icon = Icons.Filled.Star
    override val route = "leaderboard"
}