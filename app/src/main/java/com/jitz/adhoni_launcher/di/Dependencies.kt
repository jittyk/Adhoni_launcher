package com.jitz.adhoni_launcher.di

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.jitz.adhoni_launcher.BuildConfig
import com.jitz.adhoni_launcher.data.ParentRepository
import com.jitz.adhoni_launcher.data.StoryGeneratorService
import com.jitz.adhoni_launcher.data.StoryRepository as LocalStoryRepo
import com.jitz.adhoni_launcher.data.repository.AppRepositoryImpl
import com.jitz.adhoni_launcher.data.repository.StoryRepositoryImpl
import com.jitz.adhoni_launcher.domain.repository.AppRepository
import com.jitz.adhoni_launcher.domain.repository.StoryRepository
import com.jitz.adhoni_launcher.domain.usecase.GenerateAiStoryUseCase
import com.jitz.adhoni_launcher.domain.usecase.GetAllowedAppsUseCase
import com.jitz.adhoni_launcher.domain.usecase.GetStoriesUseCase
import com.jitz.adhoni_launcher.domain.usecase.ToggleAppPermissionUseCase
import com.jitz.adhoni_launcher.security.BiometricAuthManager

/**
 * Service Locator for dependency injection
 * Manages all application dependencies in one place
 */
object Dependencies {
    // Core dependencies
    private var parentRepository: ParentRepository? = null
    private var biometricAuthManager: BiometricAuthManager? = null

    // Domain repositories (implementations)
    private var appRepository: AppRepository? = null
    private var storyRepository: StoryRepository? = null

    // Use cases
    private var getAllowedAppsUseCase: GetAllowedAppsUseCase? = null
    private var toggleAppPermissionUseCase: ToggleAppPermissionUseCase? = null
    private var getStoriesUseCase: GetStoriesUseCase? = null
    private var generateAiStoryUseCase: GenerateAiStoryUseCase? = null

    /**
     * Initialize all dependencies
     * Call this once in MainActivity.onCreate()
     */
    fun init(context: Context, activity: FragmentActivity, apiKey: String = BuildConfig.STORY_API_KEY) {
        // Initialize core dependencies
        parentRepository = ParentRepository(context)
        biometricAuthManager = BiometricAuthManager(activity)

        // Initialize repository implementations
        appRepository = AppRepositoryImpl(context, parentRepository!!)
        storyRepository = StoryRepositoryImpl(
            LocalStoryRepo(),
            StoryGeneratorService(apiKey)
        )

        // Initialize use cases
        getAllowedAppsUseCase = GetAllowedAppsUseCase(appRepository!!)
        toggleAppPermissionUseCase = ToggleAppPermissionUseCase(appRepository!!)
        getStoriesUseCase = GetStoriesUseCase(storyRepository!!)
        generateAiStoryUseCase = GenerateAiStoryUseCase(storyRepository!!)
    }

    // ============ Getters for dependencies ============

    fun getParentRepository(): ParentRepository =
        parentRepository ?: throw IllegalStateException("Dependencies not initialized. Call Dependencies.init() first")

    fun getBiometricAuthManager(): BiometricAuthManager =
        biometricAuthManager ?: throw IllegalStateException("Dependencies not initialized. Call Dependencies.init() first")

    fun getGetAllowedAppsUseCase(): GetAllowedAppsUseCase =
        getAllowedAppsUseCase ?: throw IllegalStateException("Dependencies not initialized. Call Dependencies.init() first")

    fun getToggleAppPermissionUseCase(): ToggleAppPermissionUseCase =
        toggleAppPermissionUseCase ?: throw IllegalStateException("Dependencies not initialized. Call Dependencies.init() first")

    fun getGetStoriesUseCase(): GetStoriesUseCase =
        getStoriesUseCase ?: throw IllegalStateException("Dependencies not initialized. Call Dependencies.init() first")

    fun getGenerateAiStoryUseCase(): GenerateAiStoryUseCase =
        generateAiStoryUseCase ?: throw IllegalStateException("Dependencies not initialized. Call Dependencies.init() first")
}
