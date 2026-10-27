package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ai.EvaluationResult
import com.example.data.ai.GeminiAIService
import com.example.ui.components.SourceBadge
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.SoftGold
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakingPracticeScreen(
    lessonId: Int,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()
    var currentLessonId by remember(lessonId) { mutableIntStateOf(lessonId) }
    val lesson = lessons.find { it.id == currentLessonId } ?: lessons.firstOrNull()

    val contentsFlow = remember(currentLessonId) { viewModel.repository.getContentsForLesson(currentLessonId) }
    val contents by contentsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    // Target dialogue or reading passage from this lesson
    val targetContent = remember(contents) {
        contents.firstOrNull { it.contentType == "DIALOGUE" }
            ?: contents.firstOrNull { it.contentType == "READING" }
            ?: contents.firstOrNull()
    }

    val targetText = targetContent?.contentText ?: "Could you please help me with this problem?"

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var recognizedSpeech by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var isEvaluating by remember { mutableStateOf(false) }
    var evaluationResult by remember { mutableStateOf<EvaluationResult?>(null) }
    var permissionDenied by remember { mutableStateOf(false) }

    // TTS for reading the native dialogue
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    // Android SpeechRecognizer
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    DisposableEffect(Unit) {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        isListening = false
                    }
                    override fun onError(error: Int) {
                        isListening = false
                    }
                    override fun onResults(results: Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            recognizedSpeech = matches[0]
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            recognizedSpeech = matches[0]
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
            speechRecognizer = recognizer
        }
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        recognizedSpeech = ""
        isListening = true
        evaluationResult = null
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        isListening = false
        speechRecognizer?.stopListening()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionDenied = false
            startListening()
        } else {
            permissionDenied = true
        }
    }

    fun handleMicClick() {
        if (isListening) {
            stopListening()
        } else {
            val hasPerm = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPerm) {
                startListening()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Speaking & Reading Evaluator",
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
                    IconButton(onClick = onBack, modifier = Modifier.testTag("speaking_back_btn")) {
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
            // Header instruction card
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
                                text = "Speaking & Pronunciation Practice",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            SourceBadge(isOriginal = true, page = targetContent?.sourcePage)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "اقرأ النص أو المحادثة التالية بصوتك عبر الميكروفون، وسيقوم الذكاء الاصطناعي (Gemini AI) بتحليل نطقك وطلاقتك وتقديم تقييم دقيق وتصحيحات فورية.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Lesson Selector Bar (1 to 11)
            item {
                Column {
                    Text(
                        text = "Choose Lesson (1 – 11):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DeepNavyPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ScrollableTabRow(
                        selectedTabIndex = (lesson?.lessonNumber ?: 1) - 1,
                        edgePadding = 0.dp,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        lessons.forEach { l ->
                            Tab(
                                selected = l.id == currentLessonId,
                                onClick = {
                                    if (l.id != currentLessonId) {
                                        currentLessonId = l.id
                                        recognizedSpeech = ""
                                        evaluationResult = null
                                    }
                                },
                                text = { Text("L${l.lessonNumber}", fontSize = 12.sp, fontWeight = if (l.id == currentLessonId) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }
                }
            }

            // Target Text to Read
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
                                text = "Target Dialogue / Passage to Read:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = AccentTeal
                            )
                            IconButton(
                                onClick = {
                                    tts?.speak(targetText, TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Listen to native audio",
                                    tint = DeepNavyPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = targetText,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            // Interactive Mic Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isListening) AccentTeal.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(if (isListening) Color(0xFFD32F2F) else DeepNavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = { handleMicClick() },
                                modifier = Modifier
                                    .size(72.dp)
                                    .testTag("btn_mic_record")
                            ) {
                                Icon(
                                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = "Microphone",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isListening) "Listening... Speak now in English" else "Tap microphone to start speaking",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isListening) Color(0xFFD32F2F) else DeepNavyPrimary
                        )

                        if (permissionDenied) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Microphone permission is required for speech recognition.",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Live recognized transcript & AI submission
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "What You Said (Speech Transcript):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DeepNavyPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = recognizedSpeech,
                            onValueChange = { recognizedSpeech = it },
                            placeholder = { Text("Your spoken words will appear here. You can also edit manually...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp)
                                .testTag("transcript_input_field"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (recognizedSpeech.isNotBlank()) {
                                    isEvaluating = true
                                    coroutineScope.launch {
                                        val res = GeminiAIService.evaluateSpeech(
                                            targetDialogue = targetText,
                                            spokenTranscript = recognizedSpeech,
                                            lessonTitle = lesson?.title ?: "Intermediate Lesson"
                                        )
                                        evaluationResult = res
                                        isEvaluating = false
                                    }
                                }
                            },
                            enabled = !isEvaluating && recognizedSpeech.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_submit_speech_eval"),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
                        ) {
                            if (isEvaluating) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI Evaluating Your Pronunciation...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Evaluate with Gemini AI")
                            }
                        }
                    }
                }
            }

            // AI Evaluation Result Display Card
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
                            .testTag("ai_speech_eval_result_card")
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
                                        text = "AI Assessment Result",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (res.score >= 75) CorrectGreen else SoftGold
                                ) {
                                    Text(
                                        text = "${res.score} / 100",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
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
                                    text = "Notes & Corrections:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                                res.corrections.forEach { c ->
                                    Text(
                                        text = "• $c",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (res.suggestions.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Tip: ${res.suggestions}",
                                        fontSize = 12.sp,
                                        color = AccentTeal,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(10.dp)
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
