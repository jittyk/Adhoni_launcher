package com.jitz.adhoni_launcher.domain.model

enum class StoryCategory {
    BEDTIME,
    ADVENTURE,
    FANTASY,
    FUNNY
}

data class DomainStory(
    val id: String,
    val title: String,
    val content: String,
    val durationText: String,
    val category: StoryCategory
)