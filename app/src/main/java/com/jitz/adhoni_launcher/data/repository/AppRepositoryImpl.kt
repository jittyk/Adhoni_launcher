package com.jitz.adhoni_launcher.data.repository

import android.content.Context
import android.content.Intent
import com.jitz.adhoni_launcher.data.ParentRepository
import com.jitz.adhoni_launcher.domain.model.DomainAppItem
import com.jitz.adhoni_launcher.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AppRepositoryImpl(
    private val context: Context,
    private val parentRepository: ParentRepository
) : AppRepository {

    override fun getInstalledApps(): Flow<List<DomainAppItem>> =
        combine(parentRepository.allowedApps) { allowedSet ->
            val packageManager = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            packageManager.queryIntentActivities(intent, 0).map { resolveInfo ->
                val pkg = resolveInfo.activityInfo.packageName
                DomainAppItem(
                    packageName = pkg,
                    label = resolveInfo.loadLabel(packageManager).toString(),
                    isAllowed = allowedSet.contains(pkg)
                )
            }.sortedBy { it.label.lowercase() }
        }

    override suspend fun setAppPermission(packageName: String, isAllowed: Boolean) {
        parentRepository.toggleAppPermission(packageName, isAllowed)
    }
}
