package com.jitz.adhoni_launcher.domain.repository

import com.jitz.adhoni_launcher.domain.model.DomainStory
import com.jitz.adhoni_launcher.domain.model.StoryCategory

interface StoryRepository {
    fun getSampleStories(): List<DomainStory>
    suspend fun generateStory(category: StoryCategory): Result<DomainStory>
}