package com.jitz.adhoni_launcher.domain.usecase

import com.jitz.adhoni_launcher.domain.repository.AppRepository

class ToggleAppPermissionUseCase(
    private val repository: AppRepository
) {
    suspend operator fun invoke(packageName: String, isAllowed: Boolean) {
        repository.setAppPermission(packageName, isAllowed)
    }
}