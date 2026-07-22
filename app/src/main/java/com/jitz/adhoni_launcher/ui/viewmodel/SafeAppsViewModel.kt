package com.jitz.adhoni_launcher.ui.viewmodel

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jitz.adhoni_launcher.domain.usecase.GetAllowedAppsUseCase
import com.jitz.adhoni_launcher.domain.usecase.ToggleAppPermissionUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppModel(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    val isAllowed: Boolean
)

class SafeAppsViewModel(
    private val getAllowedAppsUseCase: GetAllowedAppsUseCase,
    private val toggleAppPermissionUseCase: ToggleAppPermissionUseCase,
    private val context: Context
) : ViewModel() {

    val appList: StateFlow<List<AppModel>> = getAllowedAppsUseCase()
        .map { domainApps ->
            domainApps.map { app ->
                val icon = context.packageManager.getApplicationIcon(app.packageName)
                AppModel(
                    label = app.label,
                    packageName = app.packageName,
                    icon = icon,
                    isAllowed = app.isAllowed
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleAppPermission(packageName: String, isAllowed: Boolean) {
        viewModelScope.launch {
            toggleAppPermissionUseCase(packageName, isAllowed)
        }
    }
}

class SafeAppsViewModelFactory(
    private val getAllowedAppsUseCase: GetAllowedAppsUseCase,
    private val toggleAppPermissionUseCase: ToggleAppPermissionUseCase,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SafeAppsViewModel(
            getAllowedAppsUseCase,
            toggleAppPermissionUseCase,
            context
        ) as T
    }
}
