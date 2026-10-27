package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DashboardActionCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit
) {
    val counts by viewModel.counts.collectAsStateWithLifecycle()
    val completedCount by viewModel.completedLessonsCount.collectAsStateWithLifecycle()
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()
    val assessments by viewModel.assessments.collectAsStateWithLifecycle()

    val progressPercent = if (counts.lessons > 0) {
        ((completedCount.toFloat() / counts.lessons.toFloat()) * 100).toInt()
    } else 0

    val lastAssessment = assessments.firstOrNull()
    val lastOpenedLesson = lessons.firstOrNull { it.isCompleted || it.lastReadPosition > 0 } ?: lessons.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavyPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(DeepNavyPrimary, Color(0xFF1B4965))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AccentTeal.copy(alpha = 0.25f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = "Intermediate Level",
                                        tint = AccentTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Intermediate Level 2",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onNavigate(Screen.Settings.route) },
                                modifier = Modifier.testTag("home_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "English Intermediate Master",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Learn, Practice, Review, and Test Your English",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Overall Progress Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Course Progress ($completedCount / ${counts.lessons} Lessons)",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "$progressPercent%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SoftGold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = AccentTeal,
                            trackColor = Color.White.copy(alpha = 0.2f),
                        )
                    }
                }
            }
        }

        // Live Educational Content Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricChip(
                    label = "PDF Lessons",
                    count = counts.lessons.toString(),
                    icon = Icons.Default.MenuBook,
                    modifier = Modifier.weight(1f)
                )
                MetricChip(
                    label = "Grammar Rules",
                    count = counts.grammar.toString(),
                    icon = Icons.Default.AutoStories,
                    modifier = Modifier.weight(1f)
                )
                MetricChip(
                    label = "Questions Bank",
                    count = counts.questions.toString(),
                    icon = Icons.Default.Quiz,
                    modifier = Modifier.weight(1f)
                )
                MetricChip(
                    label = "Vocabulary",
                    count = counts.vocabulary.toString(),
                    icon = Icons.Default.Translate,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Continue Learning
        if (lastOpenedLesson != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("continue_learning_card")
                        .clickable {
                            onNavigate(Screen.LessonDetail.createRoute(lastOpenedLesson.id))
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(DeepNavyPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "L${lastOpenedLesson.lessonNumber}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Continue Learning",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentTeal
                            )
                            Text(
                                text = lastOpenedLesson.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = lastOpenedLesson.titleAr,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open lesson",
                            tint = DeepNavyPrimary
                        )
                    }
                }
            }
        }

        // Primary Module Actions
        item {
            Text(
                text = "Interactive Learning Modules",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardActionCard(
                        title = "Course Lessons",
                        subtitle = "11 structured PDF units",
                        icon = Icons.Default.MenuBook,
                        containerColor = DeepNavyPrimaryContainer,
                        contentColor = DeepNavyOnPrimaryContainer,
                        testTag = "btn_nav_lessons",
                        onClick = { onNavigate(Screen.Lessons.route) },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardActionCard(
                        title = "Grammar Library",
                        subtitle = "Rules & Arabic explanations",
                        icon = Icons.Default.MenuBook,
                        containerColor = AccentTealContainer,
                        contentColor = AccentTealOnContainer,
                        testTag = "btn_nav_grammar",
                        onClick = { onNavigate(Screen.Grammar.route) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardActionCard(
                        title = "Question Bank",
                        subtitle = "Study & Test modes",
                        icon = Icons.Default.Quiz,
                        containerColor = SlateSecondaryContainer,
                        contentColor = SlateOnSecondaryContainer,
                        testTag = "btn_nav_questions",
                        onClick = { onNavigate(Screen.Questions.route) },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardActionCard(
                        title = "Matching Exercises",
                        subtitle = "Pairs & interactive connect",
                        icon = Icons.Default.CompareArrows,
                        containerColor = SoftGoldContainer,
                        contentColor = Color(0xFF6B4300),
                        testTag = "btn_nav_matching",
                        onClick = { onNavigate(Screen.Matching.route) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardActionCard(
                        title = "Vocabulary",
                        subtitle = "Flashcards & Arabic meanings",
                        icon = Icons.Default.Translate,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        testTag = "btn_nav_vocabulary",
                        onClick = { onNavigate(Screen.Vocabulary.route) },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardActionCard(
                        title = "Assessment",
                        subtitle = "Level & Final course exam",
                        icon = Icons.Default.Assessment,
                        containerColor = DeepNavyPrimary,
                        contentColor = Color.White,
                        testTag = "btn_nav_assessment",
                        onClick = { onNavigate(Screen.Assessment.route) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardActionCard(
                        title = "AI Speaking Lab",
                        subtitle = "Read aloud & speech review",
                        icon = Icons.Default.Mic,
                        containerColor = AccentTealContainer,
                        contentColor = AccentTealOnContainer,
                        testTag = "btn_nav_speaking_lab",
                        onClick = { onNavigate(Screen.SpeakingPractice.createRoute(1)) },
                        modifier = Modifier.weight(1f)
                    )
                    DashboardActionCard(
                        title = "AI Writing Studio",
                        subtitle = "Essay grading & feedback",
                        icon = Icons.Default.Edit,
                        containerColor = SoftGoldContainer,
                        contentColor = Color(0xFF6B4300),
                        testTag = "btn_nav_writing_studio",
                        onClick = { onNavigate(Screen.WritingPractice.createRoute(1)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Recent Assessment Result Banner (if exists)
        if (lastAssessment != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recent_assessment_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AccentTeal.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Assessment score",
                            tint = AccentTeal,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Latest Assessment Result",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentTeal
                            )
                            Text(
                                text = "${lastAssessment.title}: ${lastAssessment.score}/${lastAssessment.totalQuestions} (${lastAssessment.percentage}%)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricChip(
    label: String,
    count: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = AccentTeal,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}
