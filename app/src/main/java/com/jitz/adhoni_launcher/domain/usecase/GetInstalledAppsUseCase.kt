package com.jitz.adhoni_launcher.domain.usecase

import com.jitz.adhoni_launcher.domain.model.DomainAppItem
import com.jitz.adhoni_launcher.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow

class GetInstalledAppsUseCase(
    private val repository: AppRepository
) {
    operator fun invoke(): Flow<List<DomainAppItem>> {
        return repository.getInstalledApps()
    }
}
