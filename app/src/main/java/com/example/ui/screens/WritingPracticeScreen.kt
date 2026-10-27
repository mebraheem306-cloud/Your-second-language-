package com.example.ui.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ai.EvaluationResult
import com.example.data.ai.GeminiAIService
import com.example.data.ai.TaskStatus
import com.example.data.ai.WritingTaskRegistry
import com.example.ui.components.SourceBadge
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.SoftGold
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingPracticeScreen(
    lessonId: Int,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()
    var currentLessonId by remember(lessonId) { mutableIntStateOf(lessonId) }
    val lesson = lessons.find { it.id == currentLessonId } ?: lessons.firstOrNull()
    val lessonNumber = lesson?.lessonNumber ?: 1

    // Independent drafts and evaluation results per lesson to prevent cross-lesson contamination
    val lessonDrafts = remember { mutableStateMapOf<Int, String>() }
    val lessonResults = remember { mutableStateMapOf<Int, EvaluationResult?>() }

    var studentEssay by remember { mutableStateOf(lessonDrafts[currentLessonId] ?: "") }
    var isEvaluating by remember { mutableStateOf(false) }
    var evaluationResult by remember { mutableStateOf<EvaluationResult?>(lessonResults[currentLessonId]) }
    var evaluationError by remember { mutableStateOf<String?>(null) }
    var submissionSequence by remember { mutableLongStateOf(0L) }

    // Accurate, unified task specification for the currently active lesson
    val taskSpec = remember(lessonNumber) {
        WritingTaskRegistry.getSpecificationForLesson(lessonNumber)
    }

    val coroutineScope = rememberCoroutineScope()

    val wordCount = remember(studentEssay) {
        studentEssay.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Writing Workshop",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        lesson?.let {
                            Text(
                                text = "Lesson ${it.lessonNumber}: ${it.title}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("writing_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepNavyPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Writing Practice & AI Evaluation",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            SourceBadge(isOriginal = true, page = 1)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "اكتب موضوع التعبير والتدريب الكتابي الخاص بالدرس، وسيقوم نظام التقييم الذكي بفحص القواعد النحوية، والمفردات، والتراكيب، وتصحيح الأخطاء وتقديم نصائح بالعربية والإنجليزية.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Lesson Selector Bar (1 to 11) with clean state isolation
            item {
                Column {
                    Text(
                        text = "Choose Lesson (1 – 11):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 1..6) {
                            val selected = lesson?.lessonNumber == i
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    val target = lessons.find { it.lessonNumber == i }
                                    if (target != null && target.id != currentLessonId) {
                                        // Save draft of current lesson before switching
                                        lessonDrafts[currentLessonId] = studentEssay
                                        lessonResults[currentLessonId] = evaluationResult

                                        // Switch to new lesson
                                        currentLessonId = target.id
                                        studentEssay = lessonDrafts[target.id] ?: ""
                                        evaluationResult = lessonResults[target.id]
                                        evaluationError = null
                                        submissionSequence++
                                    }
                                },
                                label = { Text("L$i", fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 7..11) {
                            val selected = lesson?.lessonNumber == i
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    val target = lessons.find { it.lessonNumber == i }
                                    if (target != null && target.id != currentLessonId) {
                                        // Save draft of current lesson before switching
                                        lessonDrafts[currentLessonId] = studentEssay
                                        lessonResults[currentLessonId] = evaluationResult

                                        // Switch to new lesson
                                        currentLessonId = target.id
                                        studentEssay = lessonDrafts[target.id] ?: ""
                                        evaluationResult = lessonResults[target.id]
                                        evaluationError = null
                                        submissionSequence++
                                    }
                                },
                                label = { Text("L$i", fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Writing Task Prompt Card (Dynamic from Registry for currently active lesson)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = DeepNavyPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Writing Assignment (Lesson $lessonNumber):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DeepNavyPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = taskSpec.promptText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentTeal.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Target: ${taskSpec.targetGrammar}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavyPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Essay writing input area
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Composition (Lesson $lessonNumber):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DeepNavyPrimary
                            )
                            Text(
                                text = "$wordCount words",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (wordCount >= taskSpec.minWords) CorrectGreen else MaterialTheme.colorScheme.outline
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = studentEssay,
                            onValueChange = {
                                studentEssay = it
                                lessonDrafts[currentLessonId] = it
                                // Invalidate stale results immediately
                                submissionSequence++
                                evaluationResult = null
                                lessonResults[currentLessonId] = null
                                evaluationError = null
                            },
                            placeholder = { Text("Type your answer or composition here in English...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 160.dp)
                                .testTag("essay_text_field"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val currentText = studentEssay.trim()
                                if (currentText.isNotBlank()) {
                                    isEvaluating = true
                                    evaluationResult = null
                                    evaluationError = null
                                    val thisReqId = ++submissionSequence
                                    val activeLessonNum = lessonNumber
                                    val activityId = taskSpec.activityId

                                    coroutineScope.launch {
                                        Log.d(
                                            "WritingAssessment",
                                            "Grading started. Activity: $activityId, Lesson: $activeLessonNum, ReqId: $thisReqId, Text: '$currentText'"
                                        )
                                        try {
                                            val res = GeminiAIService.evaluateWriting(
                                                lessonTitle = lesson?.title ?: "Lesson",
                                                writingPrompt = taskSpec.promptText,
                                                studentText = currentText,
                                                targetGrammarTopic = taskSpec.targetGrammar,
                                                lessonNumber = activeLessonNum,
                                                activityId = activityId,
                                                submissionId = thisReqId
                                            )
                                            // Enforce that only responses belonging to the latest submission for this active lesson are shown
                                            if (thisReqId == submissionSequence && activeLessonNum == (lesson?.lessonNumber ?: 1)) {
                                                evaluationResult = res
                                                lessonResults[currentLessonId] = res
                                                evaluationError = null
                                                Log.d("WritingAssessment", "Applied latest assessment result. Score: ${res.score}/100, Status: ${res.taskStatus}")
                                            } else {
                                                Log.d("WritingAssessment", "Ignored stale assessment response for ReqId: $thisReqId")
                                            }
                                        } catch (e: Exception) {
                                            if (thisReqId == submissionSequence) {
                                                evaluationError = "يتطلب تقييم الكتابة بالذكاء الاصطناعي اتصالاً نشطاً بالإنترنت. يرجى التحقق من اتصال الإنترنت ثم إعادة المحاولة للتصحيح عبر الذكاء الاصطناعي (Gemini AI Online)."
                                                Log.e("WritingAssessment", "Evaluation error for ReqId: $thisReqId: ${e.message}")
                                            }
                                        } finally {
                                            if (thisReqId == submissionSequence) {
                                                isEvaluating = false
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isEvaluating && studentEssay.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_submit_writing_eval"),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepNavyPrimary)
                        ) {
                            if (isEvaluating) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جاري التقييم أونلاين عبر الذكاء الاصطناعي...")
                            } else {
                                Icon(Icons.Default.CloudSync, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تقييم وتحليل عبر الذكاء الاصطناعي أونلاين (Gemini AI)")
                            }
                        }
                    }
                }
            }

            // AI Service Error Notice & Retry Card (Requires Internet)
            if (evaluationError != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_eval_error_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WifiOff,
                                    contentDescription = "No Internet",
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "يلزم توفر اتصال بالإنترنت (Online AI Required)",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = evaluationError ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val currentText = studentEssay.trim()
                                    if (currentText.isNotBlank()) {
                                        isEvaluating = true
                                        evaluationResult = null
                                        evaluationError = null
                                        val thisReqId = ++submissionSequence
                                        val activeLessonNum = lessonNumber
                                        val activityId = taskSpec.activityId

                                        coroutineScope.launch {
                                            try {
                                                val res = GeminiAIService.evaluateWriting(
                                                    lessonTitle = lesson?.title ?: "Lesson",
                                                    writingPrompt = taskSpec.promptText,
                                                    studentText = currentText,
                                                    targetGrammarTopic = taskSpec.targetGrammar,
                                                    lessonNumber = activeLessonNum,
                                                    activityId = activityId,
                                                    submissionId = thisReqId
                                                )
                                                if (thisReqId == submissionSequence && activeLessonNum == (lesson?.lessonNumber ?: 1)) {
                                                    evaluationResult = res
                                                    lessonResults[currentLessonId] = res
                                                    evaluationError = null
                                                }
                                            } catch (e: Exception) {
                                                if (thisReqId == submissionSequence) {
                                                    evaluationError = "تعذر الاتصال بخوادم الذكاء الاصطناعي (${e.localizedMessage ?: "Network error"}). يرجى التأكد من اتصال الإنترنت ثم إعادة المحاولة."
                                                }
                                            } finally {
                                                if (thisReqId == submissionSequence) isEvaluating = false
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepNavyPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("إعادة المحاولة عبر الذكاء الاصطناعي أونلاين (Retry Online AI)")
                            }
                        }
                    }
                }
            }

            // AI Writing Evaluation Results Card
            if (evaluationResult != null) {
                item {
                    val res = evaluationResult!!
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (res.score >= 75) CorrectGreen.copy(alpha = 0.1f) else SoftGold.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (res.score >= 75) CorrectGreen else SoftGold,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .testTag("ai_writing_eval_result_card")
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = if (res.score >= 75) CorrectGreen else DeepNavyPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "AI Essay Assessment",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                // Guaranteed LTR formatting prevents "100 / 44" flip
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = when (res.taskStatus) {
                                            TaskStatus.COMPLETE -> CorrectGreen
                                            TaskStatus.PARTIAL -> SoftGold
                                            TaskStatus.TASK_MISMATCH -> Color(0xFF8E24AA)
                                            TaskStatus.COPIED_INSTRUCTIONS -> MaterialTheme.colorScheme.error
                                            TaskStatus.IRRELEVANT -> MaterialTheme.colorScheme.error
                                            TaskStatus.EMPTY -> MaterialTheme.colorScheme.error
                                        }
                                    ) {
                                        Text(
                                            text = GeminiAIService.formatScoreDisplay(res.score, res.maxScore),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White,
                                                textDirection = TextDirection.Ltr
                                            ),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            // Task Status Badge
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (res.taskStatus) {
                                    TaskStatus.COMPLETE -> CorrectGreen.copy(alpha = 0.15f)
                                    TaskStatus.PARTIAL -> SoftGold.copy(alpha = 0.2f)
                                    TaskStatus.TASK_MISMATCH -> Color(0xFF8E24AA).copy(alpha = 0.15f)
                                    TaskStatus.COPIED_INSTRUCTIONS -> MaterialTheme.colorScheme.errorContainer
                                    TaskStatus.IRRELEVANT -> MaterialTheme.colorScheme.errorContainer
                                    TaskStatus.EMPTY -> MaterialTheme.colorScheme.errorContainer
                                }
                            ) {
                                Text(
                                    text = when (res.taskStatus) {
                                        TaskStatus.COMPLETE -> "STATUS: COMPLETE (مكتمل)"
                                        TaskStatus.PARTIAL -> "STATUS: PARTIAL CREDIT (${res.completedItems}/${res.totalRequiredItems} إجابة جزئية)"
                                        TaskStatus.TASK_MISMATCH -> "STATUS: TASK MISMATCH (عدم تطابق مع المطلوب)"
                                        TaskStatus.COPIED_INSTRUCTIONS -> "STATUS: COPIED INSTRUCTIONS (نسخ نص السؤال دون إجابة)"
                                        TaskStatus.IRRELEVANT -> "STATUS: IRRELEVANT (غير مطابق للموضوع)"
                                        TaskStatus.EMPTY -> "STATUS: EMPTY (فارغ)"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (res.taskStatus) {
                                        TaskStatus.COMPLETE -> CorrectGreen
                                        TaskStatus.PARTIAL -> Color(0xFF8A5A00)
                                        TaskStatus.TASK_MISMATCH -> Color(0xFF6A1B9A)
                                        else -> MaterialTheme.colorScheme.error
                                    },
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 6-Metric Rubric Breakdown Grid with explicit LTR formatting
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    RubricChip("Completion", GeminiAIService.formatRubricScore(res.taskCompletionScore, 30), DeepNavyPrimary, Modifier.weight(1f))
                                    RubricChip("Grammar", GeminiAIService.formatRubricScore(res.grammarScore, 20), AccentTeal, Modifier.weight(1f))
                                    RubricChip("Vocabulary", GeminiAIService.formatRubricScore(res.vocabularyScore, 15), Color(0xFF1E88E5), Modifier.weight(1f))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    RubricChip("Organization", GeminiAIService.formatRubricScore(res.organizationScore, 10), SoftGold, Modifier.weight(1f))
                                    RubricChip("Punctuation", GeminiAIService.formatRubricScore(res.punctuationScore, 10), Color(0xFF673AB7), Modifier.weight(1f))
                                    RubricChip("Style & Register", GeminiAIService.formatRubricScore(res.styleScore, 15), Color(0xFF00897B), Modifier.weight(1f))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = res.feedbackEn,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = res.feedbackAr,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DeepNavyPrimary,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (res.corrections.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Grammar & Vocabulary Corrections:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                res.corrections.forEach { cor ->
                                    Text(
                                        text = "• $cor",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                    )
                                }
                            }

                            if (res.strengths.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Key Strengths:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CorrectGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                res.strengths.forEach { str ->
                                    Text(
                                        text = "✓ $str",
                                        fontSize = 12.sp,
                                        color = CorrectGreen,
                                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                    )
                                }
                            }

                            if (res.correctedAnswers.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Reference Model Answers (نموذج الإجابة الصحيحة):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = DeepNavyPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                res.correctedAnswers.forEach { ans ->
                                    Text(
                                        text = "★ $ans",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = DeepNavyPrimary,
                                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                    )
                                }
                            }

                            if (res.suggestions.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AccentTeal.copy(alpha = 0.12f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Writing Tip: ${res.suggestions}",
                                        fontSize = 12.sp,
                                        color = AccentTeal,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            if (res.rewrittenSample.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Polished Native Model Paragraph (النسخة المثالية):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = DeepNavyPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = AccentTeal.copy(alpha = 0.08f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = res.rewrittenSample,
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = DeepNavyPrimary,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RubricChip(
    label: String,
    scoreText: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = scoreText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = accentColor,
                    style = LocalTextStyle.current.copy(textDirection = TextDirection.Ltr)
                )
            }
        }
    }
}
