package com.jitz.adhoni_launcher.domain.usecase

import com.jitz.adhoni_launcher.domain.model.DomainAppItem
import com.jitz.adhoni_launcher.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllowedAppsUseCase(
    private val repository: AppRepository
) {
    operator fun invoke(): Flow<List<DomainAppItem>> {
        return repository.getInstalledApps().map { apps ->
            apps.filter { it.isAllowed }
        }
    }
}