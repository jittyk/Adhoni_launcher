package com.jitz.adhoni_launcher.domain.repository

import com.jitz.adhoni_launcher.domain.model.DomainAppItem
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getInstalledApps(): Flow<List<DomainAppItem>>
    suspend fun setAppPermission(packageName: String, isAllowed: Boolean)
}