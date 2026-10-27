package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SourceBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val results by viewModel.searchResults.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("Search lessons, grammar, vocabulary...") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("global_search_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Lessons Results
            if (results.lessons.isNotEmpty()) {
                item {
                    Text("Lessons (${results.lessons.size})", fontWeight = FontWeight.Bold, color = DeepNavyPrimary)
                }
                items(results.lessons) { l ->
                    SearchResultCard(
                        title = "Lesson ${l.lessonNumber}: ${l.title}",
                        subtitle = l.description,
                        type = "LESSON",
                        onClick = { onNavigate(Screen.LessonDetail.createRoute(l.id)) }
                    )
                }
            }

            // Grammar Results
            if (results.grammar.isNotEmpty()) {
                item {
                    Text("Grammar Rules (${results.grammar.size})", fontWeight = FontWeight.Bold, color = AccentTeal)
                }
                items(results.grammar) { g ->
                    SearchResultCard(
                        title = g.title,
                        subtitle = "${g.titleAr} • ${g.structure}",
                        type = "GRAMMAR",
                        onClick = { onNavigate(Screen.GrammarDetail.createRoute(g.id)) }
                    )
                }
            }

            // Vocabulary Results
            if (results.vocabulary.isNotEmpty()) {
                item {
                    Text("Vocabulary (${results.vocabulary.size})", fontWeight = FontWeight.Bold, color = DeepNavyPrimary)
                }
                items(results.vocabulary) { v ->
                    SearchResultCard(
                        title = "${v.word} (${v.partOfSpeech})",
                        subtitle = "المعنى: ${v.meaningAr} • ${v.definitionEn}",
                        type = "VOCABULARY",
                        onClick = { onNavigate(Screen.Vocabulary.route) }
                    )
                }
            }

            if (query.isNotBlank() && results.lessons.isEmpty() && results.grammar.isEmpty() && results.vocabulary.isEmpty()) {
                item {
                    Text(
                        text = "No matching items found in the course database for '$query'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    title: String,
    subtitle: String,
    type: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                SourceBadge(isOriginal = true, page = null)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
