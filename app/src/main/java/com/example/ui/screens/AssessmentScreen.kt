package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.model.AssessmentEntity
import com.example.data.model.QuestionEntity
import com.example.ui.navigation.Screen
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.IncorrectRed
import com.example.ui.theme.SoftGold
import com.example.ui.viewmodel.MainViewModel
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit
) {
    val allQuestions by viewModel.questions.collectAsStateWithLifecycle()
    val incorrectQuestions by viewModel.incorrectQuestions.collectAsStateWithLifecycle()

    var testActive by remember { mutableStateOf(false) }
    var currentTestType by remember { mutableStateOf("COURSE_FINAL") }
    var testQuestions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userAnswers by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var testCompleted by remember { mutableStateOf(false) }
    var lastSavedResult by remember { mutableStateOf<AssessmentEntity?>(null) }

    fun startTest(type: String, count: Int = 10) {
        currentTestType = type
        testQuestions = when (type) {
            "QUICK_QUIZ" -> allQuestions.shuffled().take(5.coerceAtMost(allQuestions.size))
            "GRAMMAR_FOCUS" -> allQuestions.filter { it.grammarTopic.isNotBlank() }.shuffled().take(count.coerceAtMost(allQuestions.size))
            "VOCAB_FOCUS" -> allQuestions.filter { it.questionType == "FILL_BLANK" || it.vocabularyTopic.isNotBlank() }.shuffled().take(count.coerceAtMost(allQuestions.size))
            "INCORRECT_REVIEW" -> if (incorrectQuestions.isNotEmpty()) incorrectQuestions.shuffled().take(count.coerceAtMost(incorrectQuestions.size)) else allQuestions.take(5)
            else -> allQuestions.shuffled().take(count.coerceAtMost(allQuestions.size))
        }
        currentQuestionIndex = 0
        userAnswers = emptyMap()
        testCompleted = false
        lastSavedResult = null
        testActive = true
    }

    fun submitTest() {
        var correctCount = 0
        var grammarCorrect = 0
        var grammarTotal = 0
        var vocabCorrect = 0
        var vocabTotal = 0

        val weakLessons = mutableSetOf<Int>()
        val weakGrammar = mutableSetOf<String>()

        testQuestions.forEachIndexed { index, q ->
            val userAns = userAnswers[index]
            val isCorrect = userAns != null && userAns.equals(q.correctAnswer, ignoreCase = true)
            if (isCorrect) correctCount++ else {
                weakLessons.add(q.lessonId)
                if (q.grammarTopic.isNotBlank()) weakGrammar.add(q.grammarTopic)
            }

            if (q.grammarTopic.isNotBlank()) {
                grammarTotal++
                if (isCorrect) grammarCorrect++
            }
            if (q.vocabularyTopic.isNotBlank() || q.questionType == "FILL_BLANK") {
                vocabTotal++
                if (isCorrect) vocabCorrect++
            }

            // Record attempt
            viewModel.recordQuestionAttempt(q.id, userAns ?: "UNANSWERED", isCorrect)
        }

        val total = testQuestions.size
        val percentage = if (total > 0) ((correctCount.toFloat() / total.toFloat()) * 100).toInt() else 0
        val grammarAccuracy = if (grammarTotal > 0) ((grammarCorrect.toFloat() / grammarTotal.toFloat()) * 100).toInt() else 100
        val vocabAccuracy = if (vocabTotal > 0) ((vocabCorrect.toFloat() / vocabTotal.toFloat()) * 100).toInt() else 100

        val assessment = AssessmentEntity(
            title = when (currentTestType) {
                "QUICK_QUIZ" -> "Quick Check (5 Questions)"
                "GRAMMAR_FOCUS" -> "Grammar Mastery Assessment"
                "VOCAB_FOCUS" -> "Vocabulary Practice Test"
                "INCORRECT_REVIEW" -> "Review of Past Mistakes"
                else -> "Comprehensive Course Exam"
            },
            assessmentType = currentTestType,
            score = correctCount,
            totalQuestions = total,
            percentage = percentage,
            grammarAccuracy = grammarAccuracy,
            vocabAccuracy = vocabAccuracy,
            recommendedLessonsJson = JSONArray(weakLessons.toList()).toString(),
            recommendedGrammarJson = JSONArray(weakGrammar.toList()).toString()
        )

        viewModel.saveAssessmentResult(assessment)
        lastSavedResult = assessment
        testCompleted = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (testActive) "Level Assessment" else "Assessment Center",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (testActive) {
                        IconButton(onClick = { testActive = false }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Test")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (!testActive) {
            // Assessment Launcher Menu
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DeepNavyPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Course Material Level Assessment",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Measure your mastery of all 11 Intermediate PDF units. Includes adaptive weakness analysis and lesson revision guidance.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Choose Assessment Type",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    AssessmentOptionCard(
                        title = "Comprehensive Course Final",
                        subtitle = "Full cross-lesson evaluation (10 questions)",
                        icon = Icons.Default.Assessment,
                        color = DeepNavyPrimary,
                        testTag = "btn_start_course_final",
                        onStart = { startTest("COURSE_FINAL", 10) }
                    )
                }

                item {
                    AssessmentOptionCard(
                        title = "Quick 5-Question Quiz",
                        subtitle = "Rapid check for short study sessions",
                        icon = Icons.Default.FlashOn,
                        color = AccentTeal,
                        testTag = "btn_start_quick_quiz",
                        onStart = { startTest("QUICK_QUIZ", 5) }
                    )
                }

                item {
                    AssessmentOptionCard(
                        title = "Grammar Focus Test",
                        subtitle = "Tenses, polite requests, and linking words",
                        icon = Icons.Default.AutoStories,
                        color = Color(0xFF1B4965),
                        testTag = "btn_start_grammar_test",
                        onStart = { startTest("GRAMMAR_FOCUS", 8) }
                    )
                }

                item {
                    AssessmentOptionCard(
                        title = "Review Past Mistakes",
                        subtitle = "Re-test questions answered incorrectly previously",
                        icon = Icons.Default.Replay,
                        color = IncorrectRed,
                        testTag = "btn_start_review_mistakes",
                        onStart = { startTest("INCORRECT_REVIEW", 8) }
                    )
                }
            }
        } else if (testCompleted && lastSavedResult != null) {
            // Detailed Assessment Results & Recommendations Screen
            val res = lastSavedResult!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (res.percentage >= 70) CorrectGreen.copy(alpha = 0.15f) else IncorrectRed.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (res.percentage >= 70) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (res.percentage >= 70) CorrectGreen else IncorrectRed,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "${res.score} / ${res.totalQuestions} Correct (${res.percentage}%)",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (res.percentage >= 70) CorrectGreen else IncorrectRed
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (res.percentage >= 80) "Excellent Mastery of Course Material!"
                                else if (res.percentage >= 60) "Good Effort. Review recommended topics below."
                                else "Needs Review. Follow the revision plan below.",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Performance Breakdown
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Performance Breakdown",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Grammar Accuracy:")
                                Text("${res.grammarAccuracy}%", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Vocabulary Accuracy:")
                                Text("${res.vocabAccuracy}%", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Recommendations
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, contentDescription = null, tint = AccentTeal)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Adaptive Study Recommendations",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Based on this test, revise the original PDF lessons and grammar rules for any topics missed.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = { testActive = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_finish_assessment"),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepNavyPrimary)
                    ) {
                        Text("Return to Assessment Center")
                    }
                }
            }
        } else {
            // Interactive in-progress test
            val currentQ = testQuestions.getOrNull(currentQuestionIndex)
            if (currentQ != null) {
                val options = remember(currentQ.id) {
                    try {
                        val json = JSONArray(currentQ.optionsJson)
                        val list = mutableListOf<String>()
                        for (i in 0 until json.length()) list.add(json.getString(i))
                        list
                    } catch (e: Exception) {
                        listOf("Yes", "No")
                    }
                }
                val selected = userAnswers[currentQuestionIndex]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    // Progress indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${currentQuestionIndex + 1} of ${testQuestions.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentTeal
                        )
                        Text(
                            text = "Lesson ${currentQ.lessonId}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1).toFloat() / testQuestions.size.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = AccentTeal
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = currentQ.questionText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            if (currentQ.questionTextAr.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentQ.questionTextAr,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            options.forEach { opt ->
                                val isOptSelected = selected == opt
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            userAnswers = userAnswers + (currentQuestionIndex to opt)
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isOptSelected) DeepNavyPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isOptSelected) DeepNavyPrimary else MaterialTheme.colorScheme.outlineVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(selected = isOptSelected, onClick = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = opt, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                            enabled = currentQuestionIndex > 0
                        ) {
                            Text("Previous")
                        }

                        if (currentQuestionIndex < testQuestions.size - 1) {
                            Button(
                                onClick = { currentQuestionIndex++ },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepNavyPrimary)
                            ) {
                                Text("Next")
                            }
                        } else {
                            Button(
                                onClick = { submitTest() },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                                modifier = Modifier.testTag("btn_submit_exam")
                            ) {
                                Text("Submit Exam")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssessmentOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    testTag: String,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onStart),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color)
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = color)
        }
    }
}
