package com.jitz.adhoni_launcher.domain.usecase

import com.jitz.adhoni_launcher.domain.model.DomainStory
import com.jitz.adhoni_launcher.domain.model.StoryCategory
import com.jitz.adhoni_launcher.domain.repository.StoryRepository

class GenerateAiStoryUseCase(
    private val repository: StoryRepository
) {
    suspend operator fun invoke(category: StoryCategory): Result<DomainStory> {
        return repository.generateStory(category)
    }
}