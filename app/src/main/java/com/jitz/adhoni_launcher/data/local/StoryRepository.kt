package com.jitz.adhoni_launcher.data.local

enum class StoryCategory(val displayName: String) {
    BEDTIME("Bedtime"),
    MORAL("Moral"),
    ANIMAL("Animal"),
    BIBLE("Bible"),
    ADVENTURE("Adventure")
}

data class Story(
    val id: String,
    val title: String,
    val category: StoryCategory,
    val durationText: String,
    val audioUrl: String = "",
    val coverImageRes: Int? = null,
    val content: String? = null
)

class StoryRepository {
    fun getSampleStories(): List<Story> {
        return listOf(
            Story(
                id = "1",
                title = "The Brave Little Bunny",
                category = StoryCategory.ANIMAL,
                durationText = "3 mins",
                content = "Once upon a time, in a lush green forest, lived a little bunny named Barnaby. Barnaby was smaller than his brothers, but he had a big heart. One sunny afternoon, a tiny [...]"
            ),
            Story(
                id = "2",
                title = "The Star That Couldn't Shine",
                category = StoryCategory.BEDTIME,
                durationText = "4 mins",
                content = "High up in the night sky lived Stella, a shy little star. While other stars sparkled bright and loud, Stella barely flickered. One cloudy evening, the moon was hidden, a[...]"
            ),
            Story(
                id = "3",
                title = "The Honest Woodcutter",
                category = StoryCategory.MORAL,
                durationText = "3 mins",
                content = "A hardworking woodcutter accidentally dropped his axe into a deep river. He sat by the bank, crying. Suddenly, the river spirit appeared with a golden axe and asked, 'Is[...]"
            )
        )
    }
}
