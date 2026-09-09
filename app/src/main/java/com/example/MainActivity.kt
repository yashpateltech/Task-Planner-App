package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.TaskPlannerTheme
import com.example.viewmodel.TaskViewModel
import com.example.viewmodel.TaskViewModelFactory
import kotlinx.coroutines.flow.collectLatest

enum class Screen {
    DASHBOARD,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    companion object {
        const val EXTRA_OPEN_TASK_ID = "extra_open_task_id"
    }

    private val viewModel: TaskViewModel by viewModels {
        val app = application as TaskPlannerApp
        TaskViewModelFactory(app.repository, app.dataStoreManager, applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle initial notification tap intent
        handleIntent(intent)

        setContent {
            val userPreferences by viewModel.userPreferences.collectAsStateWithLifecycle()

            TaskPlannerTheme(themeMode = userPreferences.themeMode) {
                var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
                val snackbarHostState = remember { SnackbarHostState() }

                // Check notification permission for Android 13+
                var hasNotificationPermission by remember {
                    mutableStateOf(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        } else {
                            true
                        }
                    )
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    hasNotificationPermission = isGranted
                }

                // Show UI messages via Snackbar
                LaunchedEffect(Unit) {
                    viewModel.uiMessage.collectLatest { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "ScreenTransition",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) { screen ->
                        when (screen) {
                            Screen.DASHBOARD -> {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    hasNotificationPermission = hasNotificationPermission,
                                    onRequestNotificationPermission = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    },
                                    onNavigateToSettings = {
                                        currentScreen = Screen.SETTINGS
                                    }
                                )
                            }

                            Screen.SETTINGS -> {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        currentScreen = Screen.DASHBOARD
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val taskId = intent?.getLongExtra(EXTRA_OPEN_TASK_ID, -1L) ?: -1L
        if (taskId != -1L) {
            viewModel.setFocusedTaskId(taskId)
        }
    }
}
