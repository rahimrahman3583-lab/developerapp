package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.QuranifyTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.QuranViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val quranViewModel: QuranViewModel = viewModel()
            val isDarkMode by quranViewModel.isDarkMode.collectAsState()
            val currentScreen by quranViewModel.currentScreen.collectAsState()
            val snackbarMessage by quranViewModel.snackbarMessage.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(snackbarMessage) {
                snackbarMessage?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                    quranViewModel.clearMessage()
                }
            }

            // System Back Handler
            BackHandler(enabled = currentScreen != AppScreen.HOME && currentScreen != AppScreen.SPLASH) {
                quranViewModel.navigateBack()
            }

            QuranifyTheme(darkTheme = isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        when (currentScreen) {
                            AppScreen.SPLASH -> SplashScreen(viewModel = quranViewModel)
                            AppScreen.HOME -> HomeScreen(viewModel = quranViewModel)
                            AppScreen.SURAH_LIST -> SurahListScreen(viewModel = quranViewModel)
                            AppScreen.SURAH_READING -> SurahReadingScreen(viewModel = quranViewModel)
                            AppScreen.HADITH -> HadithScreen(viewModel = quranViewModel)
                            AppScreen.AUDIO_PLAYER -> AudioPlayerScreen(viewModel = quranViewModel)
                            AppScreen.BOOKMARKS -> BookmarksScreen(viewModel = quranViewModel)
                            AppScreen.READING_HISTORY -> ReadingHistoryScreen(viewModel = quranViewModel)
                            AppScreen.SEARCH -> SearchScreen(viewModel = quranViewModel)
                            AppScreen.PRAYER_TIMES -> PrayerTimesScreen(viewModel = quranViewModel)
                            AppScreen.QIBLA -> QiblaScreen(viewModel = quranViewModel)
                            AppScreen.AUTH -> AuthScreen(viewModel = quranViewModel)
                            AppScreen.PROFILE -> ProfileScreen(viewModel = quranViewModel)
                            AppScreen.PREMIUM -> PremiumScreen(viewModel = quranViewModel)
                            AppScreen.SETTINGS -> SettingsScreen(viewModel = quranViewModel)
                            AppScreen.ADMIN_PANEL -> AdminPanelScreen(viewModel = quranViewModel)
                            AppScreen.HELP_ABOUT -> HelpAboutScreen(viewModel = quranViewModel)
                        }
                    }
                }
            }
        }
    }
}
