package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.MatchingExerciseEntity
import com.example.data.model.MatchingPairEntity
import com.example.ui.components.SourceBadge
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.IncorrectRed
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit
) {
    val exercises by viewModel.matchingExercises.collectAsStateWithLifecycle()
    var selectedExerciseIndex by remember { mutableStateOf(0) }

    val currentExercise = exercises.getOrNull(selectedExerciseIndex)
    var pairs by remember { mutableStateOf<List<MatchingPairEntity>>(emptyList()) }

    LaunchedEffect(currentExercise?.id) {
        if (currentExercise != null) {
            pairs = viewModel.repository.getPairsForExercise(currentExercise.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Matching Exercises",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Interactive Pair Connections from Courseware",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (exercises.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Exercise Selector tabs / buttons
                item {
                    ScrollableTabRow(
                        selectedTabIndex = selectedExerciseIndex,
                        edgePadding = 0.dp,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        exercises.forEachIndexed { index, ex ->
                            Tab(
                                selected = selectedExerciseIndex == index,
                                onClick = { selectedExerciseIndex = index },
                                text = { Text("L${ex.lessonId} Ex", fontSize = 12.sp) }
                            )
                        }
                    }
                }

                if (currentExercise != null && pairs.isNotEmpty()) {
                    item {
                        MatchingInteractiveSession(
                            exercise = currentExercise,
                            pairs = pairs
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchingInteractiveSession(
    exercise: MatchingExerciseEntity,
    pairs: List<MatchingPairEntity>
) {
    // Randomize the right column options while preserving original correct mapping
    val rightOptions = remember(pairs) { pairs.map { it.rightItem }.shuffled() }

    // User matches: Map of leftItem -> selected rightItem
    var userMatches by remember(pairs) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var selectedLeft by remember(pairs) { mutableStateOf<String?>(null) }
    var isSubmitted by remember(pairs) { mutableStateOf(false) }

    val correctCount = pairs.count { userMatches[it.leftItem] == it.rightItem }

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
                Text(
                    text = exercise.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                SourceBadge(isOriginal = true, page = exercise.sourcePage)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap an item on the left, then tap its matching definition on the right.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Two columns layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Items Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Group A",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = DeepNavyPrimary
                    )

                    pairs.forEach { pair ->
                        val isLeftSelected = selectedLeft == pair.leftItem
                        val matchedRight = userMatches[pair.leftItem]
                        val isCorrect = matchedRight == pair.rightItem

                        val borderColor = when {
                            isSubmitted && isCorrect -> CorrectGreen
                            isSubmitted && matchedRight != null && !isCorrect -> IncorrectRed
                            isLeftSelected -> AccentTeal
                            matchedRight != null -> DeepNavyPrimary
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isSubmitted) {
                                    selectedLeft = pair.leftItem
                                },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.5.dp, borderColor),
                            color = if (isLeftSelected) AccentTeal.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = pair.leftItem,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (matchedRight != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "-> $matchedRight",
                                        fontSize = 11.sp,
                                        color = if (isSubmitted) {
                                            if (isCorrect) CorrectGreen else IncorrectRed
                                        } else DeepNavyPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Right Items Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Group B",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = DeepNavyPrimary
                    )

                    rightOptions.forEach { rightItem ->
                        val isAlreadyMatched = userMatches.values.contains(rightItem)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isSubmitted && selectedLeft != null) {
                                    selectedLeft?.let { left ->
                                        userMatches = userMatches + (left to rightItem)
                                        selectedLeft = null
                                    }
                                },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isAlreadyMatched) DeepNavyPrimary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            color = if (isAlreadyMatched) DeepNavyPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = rightItem,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action / Score Bar
            if (!isSubmitted) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        userMatches = emptyMap()
                        selectedLeft = null
                    }) {
                        Text("Clear Matches")
                    }

                    Button(
                        onClick = { isSubmitted = true },
                        enabled = userMatches.size == pairs.size,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepNavyPrimary),
                        modifier = Modifier.testTag("submit_matching_btn")
                    ) {
                        Text("Check Matches (${userMatches.size}/${pairs.size})")
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (correctCount == pairs.size) CorrectGreen.copy(alpha = 0.15f) else AccentTeal.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Score: $correctCount / ${pairs.size} Correct",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (correctCount == pairs.size) CorrectGreen else DeepNavyPrimary
                            )
                            Text(
                                text = if (correctCount == pairs.size) "Perfect work!" else "Review your incorrect pairs above.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = {
                                userMatches = emptyMap()
                                selectedLeft = null
                                isSubmitted = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}
