package com.jitz.adhoni_launcher.domain.usecase

import com.jitz.adhoni_launcher.domain.model.DomainStory
import com.jitz.adhoni_launcher.domain.repository.StoryRepository

class GetStoriesUseCase(
    private val repository: StoryRepository
) {
    operator fun invoke(): List<DomainStory> {
        return repository.getSampleStories()
    }
}
