package com.example.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.app.core.storage.TokenStorage
import com.example.app.feature.auth.presentation.LoginScreen
import com.example.app.feature.auth.presentation.RegisterScreen
import com.example.app.feature.book.presentation.BookScreen
import com.example.app.feature.home.presentation.HomeScreen
import com.example.app.feature.music.presentation.MusicScreen
import com.example.app.feature.video.presentation.VideoScreen
import org.koin.compose.koinInject

object Routes {
    const val LOGIN    = "login"
    const val REGISTER = "register"
    const val HOME     = "home"
    const val MUSIC    = "music"
    const val MOVIES   = "movies"
    const val BOOKS    = "books"
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME,   Icons.Filled.Home,      "Home"),
    BottomNavItem(Routes.MUSIC,  Icons.Filled.MusicNote, "Music"),
    BottomNavItem(Routes.MOVIES, Icons.Filled.Movie,     "Movies"),
    BottomNavItem(Routes.BOOKS,  Icons.Filled.Book,      "Books")
)

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val tokenStorage  = koinInject<TokenStorage>()

    val isLoggedIn by tokenStorage.isLoggedIn.collectAsState(initial = null)

    if (isLoggedIn == null) return

    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route

    val showBottomBar = currentRoute in listOf(Routes.HOME, Routes.MUSIC, Routes.MOVIES, Routes.BOOKS)

    // Единая функция навигации по табам — всегда popUpTo(HOME) явно,
    // чтобы findStartDestination() не возвращал LOGIN после входа в систему.
    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(Routes.HOME) {
                saveState = true
            }
            launchSingleTop = true
            restoreState    = true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick  = { navigateToTab(item.route) },
                            icon     = { Icon(item.icon, contentDescription = item.label) },
                            label    = { Text(item.label) },
                            colors   = NavigationBarItemDefaults.colors(
                                selectedIconColor   = MaterialTheme.colorScheme.primary,
                                selectedTextColor   = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor      = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController    = navController,
            startDestination = if (isLoggedIn == true) Routes.HOME else Routes.LOGIN,
            modifier         = Modifier.padding(paddingValues)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(Routes.REGISTER) }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onLoginClick = { navController.popBackStack() }
                )
            }

            composable(Routes.HOME) {
                val scope = rememberCoroutineScope()
                HomeScreen(
                    onNavigateToMusic  = { navigateToTab(Routes.MUSIC) },
                    onNavigateToMovies = { navigateToTab(Routes.MOVIES) },
                    onNavigateToBooks  = { navigateToTab(Routes.BOOKS) },
                    onLogout = {
                        scope.launch {
                            tokenStorage.clear()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Routes.MUSIC) {
                MusicScreen()
            }

            composable(Routes.MOVIES) {
                VideoScreen()
            }

            composable(Routes.BOOKS) {
                BookScreen()
            }
        }
    }
}
