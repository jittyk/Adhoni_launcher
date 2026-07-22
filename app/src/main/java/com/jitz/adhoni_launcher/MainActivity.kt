package com.jitz.adhoni_launcher

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.*
import com.jitz.adhoni_launcher.BuildConfig
import com.jitz.adhoni_launcher.data.ParentRepository
import com.jitz.adhoni_launcher.data.Story
import com.jitz.adhoni_launcher.data.StoryGeneratorService
import com.jitz.adhoni_launcher.security.BiometricAuthManager
import com.jitz.adhoni_launcher.data.StoryRepository
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

    private lateinit var parentRepository: ParentRepository
    private lateinit var biometricAuthManager: BiometricAuthManager
    private val storyRepository = StoryRepository()

    // Replace with your NEW API key
    private val storyService = StoryGeneratorService(apiKey = BuildConfig.STORY_API_KEY)
    // Lazy viewmodel initialization ensures parentRepository exists first
    private val safeAppsViewModel: SafeAppsViewModel by viewModels {
        SafeAppsViewModelFactory(applicationContext, parentRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize late-init dependencies FIRST
        parentRepository = ParentRepository(applicationContext)
        biometricAuthManager = BiometricAuthManager(this)

        setContent {
            var currentScreen by remember { mutableStateOf(Screen.CHILD_HOME) }
            var selectedStory by remember { mutableStateOf<Story?>(null) }
            var showParentGate by remember { mutableStateOf(false) }

            val appList by safeAppsViewModel.appList.collectAsState()
            val savedPin by parentRepository.parentPin.collectAsState(initial = null)

            // Filter apps: Only show approved apps on the child home screen
            val allowedApps = appList.filter { it.isAllowed }
            val sampleStories = remember { storyRepository.getSampleStories() }

            when (currentScreen) {
                Screen.CHILD_HOME -> {
                    ChildHomeScreen(
                        allowedApps = allowedApps,
                        stories = sampleStories,
                        storyService = storyService,
                        onAppClick = { packageName ->
                            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                            if (launchIntent != null) {
                                startActivity(launchIntent)
                            } else {
                                Toast.makeText(this@MainActivity, "App cannot be opened", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onStoryClick = { story ->
                            selectedStory = story
                            currentScreen = Screen.STORY_PLAYER
                        },
                        onParentGateClick = {
                            showParentGate = true
                        }
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

            // Security Gate Modal Dialog
            if (showParentGate) {
                ParentAuthDialog(
                    correctPin = savedPin ?: "1234", // Default PIN for initial setup
                    onDismiss = { showParentGate = false },
                    onSuccess = {
                        showParentGate = false
                        currentScreen = Screen.PARENT_DASHBOARD
                    },
                    onUseBiometrics = {
                        if (biometricAuthManager.canAuthenticate()) {
                            biometricAuthManager.authenticate(
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
