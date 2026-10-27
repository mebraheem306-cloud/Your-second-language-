package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.model.GrammarRuleEntity
import com.example.ui.components.SourceBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.SoftGold
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit
) {
    val grammarRules by viewModel.grammarRules.collectAsStateWithLifecycle()
    val showArabic by viewModel.showArabic.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, UNREVIEWED, FAVORITES
    var searchQuery by remember { mutableStateOf("") }

    val filteredRules = grammarRules.filter { rule ->
        val matchesFilter = when (selectedFilter) {
            "UNREVIEWED" -> !rule.isReviewed
            "FAVORITES" -> rule.isFavorite
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                rule.title.contains(searchQuery, ignoreCase = true) ||
                rule.titleAr.contains(searchQuery, ignoreCase = true) ||
                rule.explanationEn.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Grammar Library",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Extracted from Courseware Lessons",
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
            // Search field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search grammar rules...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("grammar_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Filter chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("All (${grammarRules.size})") },
                        modifier = Modifier.testTag("filter_all")
                    )
                    FilterChip(
                        selected = selectedFilter == "UNREVIEWED",
                        onClick = { selectedFilter = "UNREVIEWED" },
                        label = { Text("To Review (${grammarRules.count { !it.isReviewed }})") },
                        modifier = Modifier.testTag("filter_unreviewed")
                    )
                    FilterChip(
                        selected = selectedFilter == "FAVORITES",
                        onClick = { selectedFilter = "FAVORITES" },
                        label = { Text("Favorites (${grammarRules.count { it.isFavorite }})") },
                        modifier = Modifier.testTag("filter_favorites")
                    )
                }
            }

            // Grammar items
            items(filteredRules, key = { it.id }) { rule ->
                GrammarRuleCard(
                    rule = rule,
                    showArabic = showArabic,
                    onOpen = { onNavigate(Screen.GrammarDetail.createRoute(rule.id)) },
                    onToggleFavorite = { viewModel.toggleGrammarFavorite(rule) },
                    onToggleReviewed = { viewModel.toggleGrammarReviewed(rule) }
                )
            }
        }
    }
}

@Composable
fun GrammarRuleCard(
    rule: GrammarRuleEntity,
    showArabic: Boolean,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleReviewed: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("grammar_card_${rule.id}")
            .clickable(onClick = onOpen),
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
                    color = DeepNavyPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Lesson ${rule.sourceLessonId}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepNavyPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SourceBadge(isOriginal = true, page = rule.sourcePage)
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (rule.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (rule.isFavorite) SoftGold else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = rule.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (showArabic && rule.titleAr.isNotBlank()) {
                Text(
                    text = rule.titleAr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentTeal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = rule.structure,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onToggleReviewed) {
                    Icon(
                        imageVector = if (rule.isReviewed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Reviewed status",
                        tint = if (rule.isReviewed) AccentTeal else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (rule.isReviewed) "Reviewed" else "Mark as Reviewed",
                        fontSize = 12.sp,
                        color = if (rule.isReviewed) AccentTeal else MaterialTheme.colorScheme.outline
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Full Details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepNavyPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open rule",
                        tint = DeepNavyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
