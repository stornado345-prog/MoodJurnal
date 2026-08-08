package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.database.AppDatabase
import com.example.data.model.MoodType
import com.example.data.repository.JournalRepository
import com.example.ui.components.BottomNavBar
import com.example.ui.components.CustomSnackbar
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TimelineScreen
import com.example.ui.theme.MoodJournalTheme
import com.example.ui.viewmodel.JournalViewModel
import com.example.ui.viewmodel.JournalViewModelFactory
import com.example.ui.viewmodel.SettingsViewModel
import com.example.ui.viewmodel.SettingsViewModelFactory
import com.example.ui.viewmodel.UiEvent
import com.example.utils.DataStoreManager
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = JournalRepository(database.journalDao())
        val dataStoreManager = DataStoreManager(applicationContext)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(dataStoreManager)
            )

            val journalViewModel: JournalViewModel = viewModel(
                factory = JournalViewModelFactory(repository)
            )

            val themeMode by settingsViewModel.themeMode.collectAsState()
            val isDynamicColor by settingsViewModel.isDynamicColor.collectAsState()
            val isOnboardingCompleted by settingsViewModel.isOnboardingCompleted.collectAsState()

            val darkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            MoodJournalTheme(
                darkTheme = darkTheme,
                dynamicColor = isDynamicColor
            ) {
                MoodJournalApp(
                    settingsViewModel = settingsViewModel,
                    journalViewModel = journalViewModel,
                    isOnboardingCompleted = isOnboardingCompleted
                )
            }
        }
    }
}

@Composable
fun MoodJournalApp(
    settingsViewModel: SettingsViewModel,
    journalViewModel: JournalViewModel,
    isOnboardingCompleted: Boolean
) {
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var snackbarMessage by remember { mutableStateOf("") }
    var isSnackbarVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        journalViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    snackbarMessage = event.message
                    isSnackbarVisible = true
                    delay(3000)
                    isSnackbarVisible = false
                }
                UiEvent.EntrySaved -> {
                    navController.navigate("timeline") {
                        popUpTo("home")
                    }
                }
                is UiEvent.NavigateToDetail -> {
                    navController.navigate("detail/${event.entryId}")
                }
            }
        }
    }

    val bottomNavRoutes = listOf("home", "timeline", "statistics", "calendar", "settings")
    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash") {
                    SplashScreen(
                        isOnboardingCompleted = isOnboardingCompleted,
                        onNavigateNext = { targetRoute ->
                            navController.navigate(targetRoute) {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    )
                }

                composable("onboarding") {
                    OnboardingScreen(
                        onComplete = {
                            settingsViewModel.setOnboardingCompleted(true)
                            navController.navigate("home") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    )
                }

                composable("home") {
                    HomeScreen(
                        viewModel = journalViewModel,
                        onNavigateToJournal = { mood ->
                            journalViewModel.prepareNewJournal(mood)
                            navController.navigate("journal")
                        },
                        onNavigateToSearch = { navController.navigate("search") },
                        onNavigateToFavorites = { navController.navigate("favorites") },
                        onNavigateToDetail = { entryId -> navController.navigate("detail/$entryId") },
                        onNavigateToTimeline = { navController.navigate("timeline") }
                    )
                }

                composable("journal") {
                    JournalScreen(
                        viewModel = journalViewModel,
                        onBackClick = { navController.popBackStack() },
                        onSavedSuccess = {
                            // Handled by UI Event
                        }
                    )
                }

                composable("timeline") {
                    TimelineScreen(
                        viewModel = journalViewModel,
                        onNavigateToDetail = { entryId -> navController.navigate("detail/$entryId") },
                        onNavigateToAdd = {
                            journalViewModel.prepareNewJournal(MoodType.HAPPY)
                            navController.navigate("journal")
                        },
                        onNavigateToSearch = { navController.navigate("search") }
                    )
                }

                composable(
                    route = "detail/{entryId}",
                    arguments = listOf(navArgument("entryId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
                    DetailScreen(
                        entryId = entryId,
                        viewModel = journalViewModel,
                        onBackClick = { navController.popBackStack() },
                        onEditClick = { entry ->
                            navController.navigate("journal")
                        }
                    )
                }

                composable("statistics") {
                    StatisticsScreen(viewModel = journalViewModel)
                }

                composable("search") {
                    SearchScreen(
                        viewModel = journalViewModel,
                        onBackClick = { navController.popBackStack() },
                        onNavigateToDetail = { entryId -> navController.navigate("detail/$entryId") }
                    )
                }

                composable("favorites") {
                    FavoritesScreen(
                        viewModel = journalViewModel,
                        onBackClick = { navController.popBackStack() },
                        onNavigateToDetail = { entryId -> navController.navigate("detail/$entryId") }
                    )
                }

                composable("calendar") {
                    CalendarScreen(
                        viewModel = journalViewModel,
                        onNavigateToDetail = { entryId -> navController.navigate("detail/$entryId") }
                    )
                }

                composable("settings") {
                    SettingsScreen(
                        settingsViewModel = settingsViewModel,
                        journalViewModel = journalViewModel
                    )
                }
            }

            // Custom Floating Toast/Snackbar Notification
            CustomSnackbar(
                message = snackbarMessage,
                isVisible = isSnackbarVisible,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (showBottomBar) 80.dp else 24.dp)
            )
        }
    }
}
