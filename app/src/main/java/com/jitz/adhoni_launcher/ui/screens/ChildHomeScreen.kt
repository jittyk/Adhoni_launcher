package com.jitz.adhoni_launcher.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.jitz.adhoni_launcher.data.Story
import com.jitz.adhoni_launcher.data.StoryCategory
import com.jitz.adhoni_launcher.data.StoryGeneratorService
import com.jitz.adhoni_launcher.viewmodel.AppModel
import kotlinx.coroutines.launch

@Composable
fun ChildHomeScreen(
    allowedApps: List<AppModel>,
    stories: List<Story>,
    storyService: StoryGeneratorService,
    onAppClick: (String) -> Unit,
    onStoryClick: (Story) -> Unit,
    onParentGateClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isGeneratingStory by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- 1. TOP HEADER & PARENT GATE BUTTON ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello, Explorer! 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "What would you like to play or read today?",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = onParentGateClick,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Text(text = "🔒", fontSize = 24.sp)
            }
        }

        // --- 2. GEMINI AI STORY GENERATOR BANNER ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "✨ Spark a New AI Story!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap below and let Gemini write a brand-new bedtime story just for you.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        isGeneratingStory = true
                        scope.launch {
                            val result = storyService.generateStoryByCategory(StoryCategory.BEDTIME)
                            isGeneratingStory = false

                            result.onSuccess { generatedStory ->
                                onStoryClick(generatedStory)
                            }.onFailure { error ->
                                Toast.makeText(
                                    context,
                                    "Failed to generate story: ${error.localizedMessage}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    enabled = !isGeneratingStory,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isGeneratingStory) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Writing Story...")
                    } else {
                        Text("✨ Generate AI Bedtime Story")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. SAMPLE STORIES ---
        Text(
            text = "📚 Story Corner",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = stories,
                key = { story -> story.id }
            ) { story ->
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .height(110.dp)
                        .clickable { onStoryClick(story) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = story.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = story.durationText,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 4. SAFE APPS GRID ---
        Text(
            text = "🎮 My Approved Apps",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (allowedApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No approved apps yet. Ask a parent to unlock apps from settings!",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = allowedApps,
                    key = { app -> app.packageName }
                ) { app ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onAppClick(app.packageName) }
                            .padding(8.dp)
                    ) {
                        // Render actual app icon instead of text fallback
                        Image(
                            painter = rememberDrawablePainter(app.icon),
                            contentDescription = app.label,
                            modifier = Modifier.size(56.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = app.label,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}