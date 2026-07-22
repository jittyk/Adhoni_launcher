package com.jitz.adhoni_launcher.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jitz.adhoni_launcher.data.ParentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppModel(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    val isAllowed: Boolean
)

class SafeAppsViewModel(
    private val context: Context,
    private val repository: ParentRepository
) : ViewModel() {

    // Read allowed packages state from DataStore
    val allowedPackages: StateFlow<Set<String>> = repository.allowedApps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val appList: StateFlow<List<AppModel>> = combine(
        repository.allowedApps
    ) { allowedSet: Array<Set<String>> ->
        val set = allowedSet.firstOrNull() ?: emptySet()
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        packageManager.queryIntentActivities(intent, 0).map { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            AppModel(
                label = resolveInfo.loadLabel(packageManager).toString(),
                packageName = pkg,
                icon = resolveInfo.loadIcon(packageManager),
                isAllowed = set.contains(pkg)
            )
        }.sortedBy { it.label.lowercase() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun toggleAppPermission(packageName: String, isAllowed: Boolean) {
        viewModelScope.launch {
            repository.toggleAppPermission(packageName, isAllowed)
        }
    }
}

class SafeAppsViewModelFactory(
    private val context: Context,
    private val repository: ParentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SafeAppsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SafeAppsViewModel(context, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}