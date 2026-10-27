package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LessonContentEntity
import com.example.ui.components.SourceBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.SoftGold
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    lessonId: Int,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()
    val lesson = lessons.find { it.id == lessonId }

    val contentsFlow = remember(lessonId) { viewModel.repository.getContentsForLesson(lessonId) }
    val contents by contentsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = lesson?.title ?: "Lesson Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        lesson?.let {
                            Text(
                                text = "Lesson ${it.lessonNumber} • Level Two",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("lesson_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    lesson?.let { l ->
                        IconButton(onClick = { viewModel.toggleLessonBookmark(l) }) {
                            Icon(
                                imageVector = if (l.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (l.isBookmarked) SoftGold else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { viewModel.toggleLessonCompleted(l) }) {
                            Icon(
                                imageVector = if (l.isCompleted) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                                contentDescription = "Toggle Complete",
                                tint = if (l.isCompleted) AccentTeal else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val prevLesson = lessons.find { it.lessonNumber == (lesson?.lessonNumber ?: 1) - 1 }
                    val nextLesson = lessons.find { it.lessonNumber == (lesson?.lessonNumber ?: 1) + 1 }

                    OutlinedButton(
                        onClick = {
                            prevLesson?.let { onNavigate(Screen.LessonDetail.createRoute(it.id)) }
                        },
                        enabled = prevLesson != null,
                        modifier = Modifier.testTag("btn_prev_lesson")
                    ) {
                        Text("Previous")
                    }

                    Button(
                        onClick = {
                            nextLesson?.let { onNavigate(Screen.LessonDetail.createRoute(it.id)) }
                        },
                        enabled = nextLesson != null,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepNavyPrimary),
                        modifier = Modifier.testTag("btn_next_lesson")
                    ) {
                        Text("Next Lesson")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Source Courseware Meta Banner
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF Source",
                            tint = DeepNavyPrimary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Source File: ${lesson?.sourceFilename ?: "Courseware.pdf"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Authentic Level Two PDF Curriculum (${lesson?.pageCount} pages)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        SourceBadge(isOriginal = true, page = null)
                    }
                }
            }

            // AI Practice Modules (Speaking & Writing)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_lesson_speaking_practice"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AccentTeal.copy(alpha = 0.15f)),
                        onClick = {
                            lesson?.let { onNavigate(Screen.SpeakingPractice.createRoute(it.id)) }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Speaking",
                                tint = AccentTeal,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "AI Speaking",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DeepNavyPrimary
                                )
                                Text(
                                    text = "Read Dialogue",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_lesson_writing_practice"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DeepNavyPrimary.copy(alpha = 0.12f)),
                        onClick = {
                            lesson?.let { onNavigate(Screen.WritingPractice.createRoute(it.id)) }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Writing",
                                tint = DeepNavyPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "AI Writing",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DeepNavyPrimary
                                )
                                Text(
                                    text = "Write Essay",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Structured Content Sections
            items(contents, key = { it.id }) { contentItem ->
                LessonContentCard(contentItem)
            }
        }
    }
}

@Composable
fun LessonContentCard(content: LessonContentEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (content.contentType) {
                        "GRAMMAR" -> DeepNavyPrimary.copy(alpha = 0.12f)
                        "READING" -> AccentTeal.copy(alpha = 0.12f)
                        "DIALOGUE" -> SoftGold.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = content.contentType,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (content.contentType) {
                            "GRAMMAR" -> DeepNavyPrimary
                            "READING" -> AccentTeal
                            "DIALOGUE" -> Color(0xFF7A4A00)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                SourceBadge(isOriginal = content.isOriginalPdf, page = content.sourcePage)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content.contentText,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}
