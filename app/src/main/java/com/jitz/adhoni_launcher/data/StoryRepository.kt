package com.jitz.adhoni_launcher.data

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
                content = "Once upon a time, in a lush green forest, lived a little bunny named Barnaby. Barnaby was smaller than his brothers, but he had a big heart. One sunny afternoon, a tiny bird lost its nest. Barnaby didn't hesitate—he gathered soft moss and sturdy twigs to help rebuild it. From that day on, all the forest animals knew that true bravery isn't about size, but kindness."
            ),
            Story(
                id = "2",
                title = "The Star That Couldn't Shine",
                category = StoryCategory.BEDTIME,
                durationText = "4 mins",
                content = "High up in the night sky lived Stella, a shy little star. While other stars sparkled bright and loud, Stella barely flickered. One cloudy evening, the moon was hidden, and a lost ship needed guidance. Stella took a deep breath and let her gentle, warm glow illuminate the water. She learned that even a quiet light can guide someone safely home."
            ),
            Story(
                id = "3",
                title = "The Honest Woodcutter",
                category = StoryCategory.MORAL,
                durationText = "3 mins",
                content = "A hardworking woodcutter accidentally dropped his axe into a deep river. He sat by the bank, crying. Suddenly, the river spirit appeared with a golden axe and asked, 'Is this yours?' 'No,' said the woodcutter. The spirit then showed a silver axe, but he declined again. Finally, showing his iron axe, the woodcutter smiled, 'Yes, that's mine!' Impressed by his honesty, the spirit gifted him all three axes."
            )
        )
    }
}