package com.example.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.VocabularyEntity
import com.example.ui.components.SourceBadge
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.SoftGold
import com.example.ui.viewmodel.MainViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit
) {
    val vocabulary by viewModel.vocabulary.collectAsStateWithLifecycle()
    val showArabicSetting by viewModel.showArabic.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var hideMeaningsMode by remember { mutableStateOf(false) } // Flashcard practice mode

    // Local Text to speech initialization
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Initialize default English locale
            }
        }
        ttsInstance.language = Locale.US
        tts = ttsInstance

        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    val filteredList = vocabulary.filter { item ->
        searchQuery.isBlank() ||
                item.word.contains(searchQuery, ignoreCase = true) ||
                item.meaningAr.contains(searchQuery, ignoreCase = true) ||
                item.definitionEn.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Course Vocabulary",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${filteredList.size} Words Extracted from Lessons",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search & Flashcard Toggle
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search words, Arabic meanings...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("vocab_search_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Flashcard Mode (Hide Meanings)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = hideMeaningsMode,
                            onCheckedChange = { hideMeaningsMode = it },
                            modifier = Modifier.testTag("flashcard_switch")
                        )
                    }
                }
            }

            // Word cards
            items(filteredList, key = { it.id }) { vocab ->
                VocabCard(
                    vocab = vocab,
                    globalHideMeanings = hideMeaningsMode,
                    showArabicSetting = showArabicSetting,
                    onPlayAudio = {
                        tts?.speak(vocab.word, TextToSpeech.QUEUE_FLUSH, null, null)
                    },
                    onToggleFavorite = { viewModel.toggleVocabFavorite(vocab) }
                )
            }
        }
    }
}

@Composable
fun VocabCard(
    vocab: VocabularyEntity,
    globalHideMeanings: Boolean,
    showArabicSetting: Boolean,
    onPlayAudio: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    var isRevealed by remember(globalHideMeanings, vocab.id) { mutableStateOf(!globalHideMeanings) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vocab_card_${vocab.id}")
            .clickable {
                if (globalHideMeanings) isRevealed = !isRevealed
            },
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = vocab.word,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepNavyPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onPlayAudio, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Pronounce ${vocab.word}",
                            tint = AccentTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SourceBadge(isOriginal = true, page = vocab.sourcePage)
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (vocab.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (vocab.isFavorite) SoftGold else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "${vocab.partOfSpeech} • Lesson ${vocab.sourceLessonId}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isRevealed) {
                Text(
                    text = vocab.definitionEn,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (showArabicSetting && vocab.meaningAr.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "المعنى: ${vocab.meaningAr}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentTeal
                    )
                }

                if (vocab.exampleSentence.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"${vocab.exampleSentence}\"",
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentTeal.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tap to reveal definition & Arabic meaning",
                        fontSize = 12.sp,
                        color = AccentTeal,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
