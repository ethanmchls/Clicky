package com.ethanmichaelis.clicky.nav

import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.ethanmichaelis.clicky.ClickyViewModel
import com.ethanmichaelis.clicky.nav.AuthType.LOGIN
import com.ethanmichaelis.clicky.nav.AuthType.REGISTER
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.flow.first
import timber.log.Timber

@Composable
fun ClickyNavHost(
    navController: NavHostController,
    viewModel: ClickyViewModel,
) {
    val score by viewModel.score.collectAsState()
    val loggedIn by viewModel.loggedIn.collectAsState()
    val signupRequested by viewModel.signupRequested.collectAsState()
    val rank by viewModel.rank.collectAsState()
    val topTen by viewModel.topTen.collectAsState()

    var loginTrigger by rememberSaveable { mutableStateOf(false) }
    val sessionState by viewModel.sessionFlow.collectAsState()
    (sessionState as? SessionStatus.Authenticated)?.let { session ->
        if (loginTrigger) {
            LaunchedEffect(Unit) {
                viewModel.sessionLogin(session.session)
                val route = if (viewModel.username.first().isNotBlank()) Clicky.route else Username.path(true)
                navController.navigate(route) {
                    popUpTo(route) {
                        inclusive = true
                    }
                }
                loginTrigger = false
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (loggedIn) Clicky.route else Login.route
    ) {
        composable(route = Login.route) {
            LoginScreen(
                loginClick = { navController.navigate(Auth.path(LOGIN)) },
                signUpClick = { navController.navigate(Auth.path(REGISTER)) },
                signupRequested = signupRequested,
            )
        }

        dialog(
            route = Auth.route,
            dialogProperties = DialogProperties(
                dismissOnBackPress = !loginTrigger,
                dismissOnClickOutside = !loginTrigger,
            ),
            arguments = listOf(navArgument("type") { type = NavType.EnumType(AuthType::class.java) })
        ) {
            val authType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.arguments?.getSerializable("type", AuthType::class.java)
            } else {
                it.arguments?.getSerializable("type") as? AuthType
            } ?: LOGIN
            AuthDialog(
                type = authType,
                loading = loginTrigger,
                submit = { email, password ->
                    viewModel.login(authType, email, password)
                    if (authType == REGISTER) {
                        navController.popBackStack()
                    } else {
                        loginTrigger = true
                    }
                },
                onBackPressed = { navController.popBackStack() }
            )
        }

        dialog(
            route = Username.route,
            dialogProperties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
            arguments = listOf(navArgument("login") { type = NavType.BoolType }),
        ) {
            val onLogin = it.arguments?.getBoolean("login") ?: false
            UsernameDialog(
                updateUsername = { username ->
                    viewModel.updateUsername(username)
                    if (onLogin) {
                        navController.navigate(Clicky.route)
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(route = Clicky.route) {
            ClickyScreen(
                score,
                setScore = {
                    viewModel.updateScore(it)
                },
                clickMenu = {
                    navController.navigate(Menu.route)
                }
            )
        }

        dialog(route = Menu.route) {
            MenuScreen(
                logoutClick = {
                    viewModel.logout()
                    navController.navigate(Login.route) {
                        popUpTo(Login.route) {
                            inclusive = true
                        }
                    }
                },
                leaderboardClick = {
                    viewModel.getRank()
                    viewModel.getTopTen()
                    navController.navigate(Leaderboard.route)
                },
                resetScoreClick = {//todo if this becomes a production feature, add "confirm" prompt
                    viewModel.updateScore(0)
//                    navController.navigate(Clicky.route)
                },
                updateUsernameClick = {
                    navController.popBackStack()
                    navController.navigate(Username.path(false))
                },
            )
        }

        composable(route = Leaderboard.route) {
            LeaderboardScreen(
                backButtonClick = {
                    if (!navController.navigateUp()) {
                        navController.navigate(Clicky.route)
                    }
                },
                getRank = rank,
                getTopTen = topTen,
//                getUsername = viewModel.getUsername(),
            )
        }
    }
}