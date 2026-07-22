package com.jitz.adhoni_launcher

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.*
import com.jitz.adhoni_launcher.di.Dependencies
import com.jitz.adhoni_launcher.domain.model.DomainStory
import com.jitz.adhoni_launcher.ui.components.ParentAuthDialog
import com.jitz.adhoni_launcher.ui.screens.ChildHomeScreen
import com.jitz.adhoni_launcher.ui.screens.ParentDashboardScreen
import com.jitz.adhoni_launcher.ui.screens.StoryPlayerScreen
import com.jitz.adhoni_launcher.viewmodel.SafeAppsViewModel
import com.jitz.adhoni_launcher.viewmodel.SafeAppsViewModelFactory

enum class Screen {
    CHILD_HOME,
    STORY_PLAYER,
    PARENT_DASHBOARD
}

class MainActivity : FragmentActivity() {

    private val safeAppsViewModel: SafeAppsViewModel by viewModels {
        SafeAppsViewModelFactory(
            Dependencies.getGetAllowedAppsUseCase(),
            Dependencies.getToggleAppPermissionUseCase(),
            applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize all dependencies ONCE
        Dependencies.init(applicationContext, this, BuildConfig.STORY_API_KEY)

        setContent {
            var currentScreen by remember { mutableStateOf(Screen.CHILD_HOME) }
            var selectedStory by remember { mutableStateOf<DomainStory?>(null) }
            var showParentGate by remember { mutableStateOf(false) }

            val appList by safeAppsViewModel.appList.collectAsState()
            val parentRepository = Dependencies.getParentRepository()
            val savedPin by parentRepository.parentPin.collectAsState(initial = null)
            
            val storiesUseCase = Dependencies.getGetStoriesUseCase()
            val sampleStories = remember { storiesUseCase() }

            when (currentScreen) {
                Screen.CHILD_HOME -> {
                    ChildHomeScreen(
                        allowedApps = appList,
                        onAppClick = { packageName ->
                            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                            if (launchIntent != null) {
                                startActivity(launchIntent)
                            } else {
                                Toast.makeText(this@MainActivity, "App cannot be opened", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onParentGateClick = { showParentGate = true }
                    )
                }

                Screen.STORY_PLAYER -> {
                    selectedStory?.let { story ->
                        StoryPlayerScreen(
                            story = story,
                            onBack = { currentScreen = Screen.CHILD_HOME }
                        )
                    }
                }

                Screen.PARENT_DASHBOARD -> {
                    ParentDashboardScreen(
                        viewModel = safeAppsViewModel,
                        onBackToLauncher = { currentScreen = Screen.CHILD_HOME }
                    )
                }
            }

            if (showParentGate) {
                ParentAuthDialog(
                    correctPin = savedPin ?: "1234",
                    onDismiss = { showParentGate = false },
                    onSuccess = {
                        showParentGate = false
                        currentScreen = Screen.PARENT_DASHBOARD
                    },
                    onUseBiometrics = {
                        val authManager = Dependencies.getBiometricAuthManager()
                        if (authManager.canAuthenticate()) {
                            authManager.authenticate(
                                onSuccess = {
                                    showParentGate = false
                                    currentScreen = Screen.PARENT_DASHBOARD
                                },
                                onError = { error ->
                                    Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Biometrics not set up on device. Use PIN.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }
}
