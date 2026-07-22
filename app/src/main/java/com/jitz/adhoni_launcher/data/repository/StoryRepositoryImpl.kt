package com.jitz.adhoni_launcher.data.repository

import com.jitz.adhoni_launcher.data.StoryGeneratorService
import com.jitz.adhoni_launcher.data.StoryRepository as LocalStoryRepo
import com.jitz.adhoni_launcher.domain.model.DomainStory
import com.jitz.adhoni_launcher.domain.model.StoryCategory
import com.jitz.adhoni_launcher.domain.repository.StoryRepository

class StoryRepositoryImpl(
    private val localStoryRepo: LocalStoryRepo,
    private val storyGeneratorService: StoryGeneratorService
) : StoryRepository {

    override fun getSampleStories(): List<DomainStory> {
        return localStoryRepo.getSampleStories().map { story ->
            DomainStory(
                id = story.id,
                title = story.title,
                content = story.content ?: "",
                durationText = story.durationText,
                category = story.category.name.let { 
                    StoryCategory.valueOf(it)
                }
            )
        }
    }

    override suspend fun generateStory(category: StoryCategory): Result<DomainStory> {
        return try {
            val story = storyGeneratorService.generateStory(category.name.lowercase())
            Result.success(
                DomainStory(
                    id = story.id,
                    title = story.title,
                    content = story.content ?: "",
                    durationText = story.durationText,
                    category = category
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
