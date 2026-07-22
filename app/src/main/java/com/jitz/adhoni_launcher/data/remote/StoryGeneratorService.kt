package com.jitz.adhoni_launcher.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.jitz.adhoni_launcher.data.local.Story
import com.jitz.adhoni_launcher.data.local.StoryCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoryGeneratorService(private val apiKey: String) {

    // Using Gemini Flash model for maximum speed and free-tier allowance
    val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    suspend fun generateStoryByCategory(category: StoryCategory): Result<Story> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val prompt = """
                 You are a world-class children's storyteller. Write a short, engaging story for a child aged 4 to 8.
                 Category: ${category.displayName}
                 
                 Rules:
                 1. Keep it under 250 words.
                 2. Use friendly, imaginative language.
                 3. Include a clear, wholesome lesson or ending.
                 4. Output format MUST be in 2 lines:
                    Line 1: Story Title
                    Line 2: Story Body
             """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val fullText = response.text ?: throw Exception("Empty response from AI")

                val lines = fullText.trim().split("\n", limit = 2)
                val title = lines.firstOrNull()?.replace("Title:", "")?.trim() ?: "A Fun Story"
                val storyBody = lines.getOrNull(1)?.trim() ?: fullText

                Story(
                    id = System.currentTimeMillis().toString(),
                    title = title,
                    category = category,
                    durationText = "~2 mins",
                    audioUrl = "",
                    content = storyBody
                )
            }
        }
    }
}
