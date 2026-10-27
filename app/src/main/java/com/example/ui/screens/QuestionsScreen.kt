package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.QuestionEntity
import com.example.ui.components.SourceBadge
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.IncorrectRed
import com.example.ui.viewmodel.MainViewModel
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit
) {
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val showArabic by viewModel.showArabic.collectAsStateWithLifecycle()

    var isStudyMode by remember { mutableStateOf(true) }
    var selectedLessonFilter by remember { mutableStateOf(0) } // 0 = all

    val filteredQuestions = if (selectedLessonFilter == 0) {
        questions
    } else {
        questions.filter { it.lessonId == selectedLessonFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Question Bank",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${filteredQuestions.size} Verified Questions from PDF",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode toggle & filter
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isStudyMode) "Study Mode (Instant Feedback)" else "Test Mode (Hidden Answers)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DeepNavyPrimary
                            )
                            Switch(
                                checked = isStudyMode,
                                onCheckedChange = { isStudyMode = it },
                                modifier = Modifier.testTag("mode_toggle_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isStudyMode)
                                "Shows explanations and source lessons immediately after each choice."
                            else
                                "Practice under real test conditions.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Questions list
            itemsIndexed(filteredQuestions, key = { _, q -> q.id }) { index, question ->
                QuestionInteractiveCard(
                    questionIndex = index + 1,
                    question = question,
                    isStudyMode = isStudyMode,
                    showArabic = showArabic,
                    onAnswerSelected = { answer, isCorrect ->
                        viewModel.recordQuestionAttempt(question.id, answer, isCorrect)
                    }
                )
            }
        }
    }
}

@Composable
fun QuestionInteractiveCard(
    questionIndex: Int,
    question: QuestionEntity,
    isStudyMode: Boolean,
    showArabic: Boolean,
    onAnswerSelected: (String, Boolean) -> Unit
) {
    var selectedOption by remember(question.id) { mutableStateOf<String?>(null) }
    val options = remember(question.optionsJson) {
        try {
            val json = JSONArray(question.optionsJson)
            val list = mutableListOf<String>()
            for (i in 0 until json.length()) list.add(json.getString(i))
            list
        } catch (e: Exception) {
            listOf("Option A", "Option B")
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("question_card_${question.id}"),
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
                    color = AccentTeal.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Q$questionIndex • Lesson ${question.lessonId}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentTeal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                SourceBadge(isOriginal = true, page = question.sourcePage)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (showArabic && question.questionTextAr.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = question.questionTextAr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Options
            options.forEach { option ->
                val isSelected = selectedOption == option
                val isCorrect = option.equals(question.correctAnswer, ignoreCase = true)

                val borderColor = when {
                    !isStudyMode && isSelected -> DeepNavyPrimary
                    isStudyMode && isSelected && isCorrect -> CorrectGreen
                    isStudyMode && isSelected && !isCorrect -> IncorrectRed
                    isStudyMode && selectedOption != null && isCorrect -> CorrectGreen
                    else -> MaterialTheme.colorScheme.outlineVariant
                }

                val containerColor = when {
                    !isStudyMode && isSelected -> DeepNavyPrimary.copy(alpha = 0.08f)
                    isStudyMode && isSelected && isCorrect -> CorrectGreen.copy(alpha = 0.1f)
                    isStudyMode && isSelected && !isCorrect -> IncorrectRed.copy(alpha = 0.1f)
                    isStudyMode && selectedOption != null && isCorrect -> CorrectGreen.copy(alpha = 0.08f)
                    else -> MaterialTheme.colorScheme.surface
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable(enabled = selectedOption == null || !isStudyMode) {
                            selectedOption = option
                            val correct = option.equals(question.correctAnswer, ignoreCase = true)
                            onAnswerSelected(option, correct)
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = containerColor,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = if (isStudyMode && !isCorrect) IncorrectRed else DeepNavyPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        if (isStudyMode && selectedOption != null) {
                            if (isCorrect) {
                                Icon(Icons.Default.Check, contentDescription = "Correct", tint = CorrectGreen)
                            } else if (isSelected) {
                                Icon(Icons.Default.Close, contentDescription = "Incorrect", tint = IncorrectRed)
                            }
                        }
                    }
                }
            }

            // Study Mode Explanation Box
            if (isStudyMode && selectedOption != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Explanation",
                                tint = AccentTeal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Explanation & Lesson Reference",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DeepNavyPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = question.explanationEn,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                        if (showArabic && question.explanationAr.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = question.explanationAr,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
