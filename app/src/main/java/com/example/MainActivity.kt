package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.DeepNavyPrimary
import com.example.ui.theme.EnglishMasterTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

            EnglishMasterTheme(darkTheme = isDarkTheme) {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(onSplashFinished = { showSplash = false })
        return
    }

    // Maintain navigation back stack with BackHandler support
    val backStack = remember { mutableStateListOf<String>(Screen.Home.route) }
    val currentRoute = backStack.lastOrNull() ?: Screen.Home.route

    fun navigateTo(route: String) {
        if (backStack.lastOrNull() != route) {
            backStack.add(route)
        }
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
        }
    }

    // Handle Android system back gestures
    BackHandler(enabled = backStack.size > 1) {
        navigateBack()
    }

    val isTopLevelScreen = currentRoute in listOf(
        Screen.Home.route,
        Screen.Lessons.route,
        Screen.Grammar.route,
        Screen.Questions.route,
        Screen.Progress.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isTopLevelScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            if (currentRoute != Screen.Home.route) {
                                backStack.clear()
                                backStack.add(Screen.Home.route)
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("nav_bottom_home")
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Lessons.route,
                        onClick = { navigateTo(Screen.Lessons.route) },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Lessons") },
                        label = { Text("Lessons") },
                        modifier = Modifier.testTag("nav_bottom_lessons")
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Grammar.route,
                        onClick = { navigateTo(Screen.Grammar.route) },
                        icon = { Icon(Icons.Default.AutoStories, contentDescription = "Grammar") },
                        label = { Text("Grammar") },
                        modifier = Modifier.testTag("nav_bottom_grammar")
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Questions.route,
                        onClick = { navigateTo(Screen.Questions.route) },
                        icon = { Icon(Icons.Default.Quiz, contentDescription = "Practice") },
                        label = { Text("Practice") },
                        modifier = Modifier.testTag("nav_bottom_practice")
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.Progress.route,
                        onClick = { navigateTo(Screen.Progress.route) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Progress") },
                        label = { Text("Progress") },
                        modifier = Modifier.testTag("nav_bottom_progress")
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                currentRoute == Screen.Home.route -> {
                    HomeScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                }
                currentRoute == Screen.Lessons.route -> {
                    LessonsScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                }
                currentRoute.startsWith("lesson_detail/") -> {
                    val lessonId = currentRoute.removePrefix("lesson_detail/").toIntOrNull() ?: 1
                    LessonDetailScreen(
                        lessonId = lessonId,
                        viewModel = viewModel,
                        onBack = { navigateBack() },
                        onNavigate = { navigateTo(it) }
                    )
                }
                currentRoute == Screen.Grammar.route -> {
                    GrammarScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                }
                currentRoute.startsWith("grammar_detail/") -> {
                    val ruleId = currentRoute.removePrefix("grammar_detail/").toLongOrNull() ?: 1L
                    GrammarDetailScreen(
                        ruleId = ruleId,
                        viewModel = viewModel,
                        onBack = { navigateBack() }
                    )
                }
                currentRoute == Screen.Questions.route -> {
                    QuestionsScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                }
                currentRoute == Screen.Matching.route -> {
                    MatchingScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                }
                currentRoute == Screen.Vocabulary.route -> {
                    VocabularyScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                }
                currentRoute == Screen.Assessment.route -> {
                    AssessmentScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                }
                currentRoute == Screen.Progress.route -> {
                    ProgressScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                }
                currentRoute == Screen.Favorites.route -> {
                    FavoritesScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                }
                currentRoute == Screen.Settings.route -> {
                    SettingsScreen(viewModel = viewModel, onBack = { navigateBack() }, onNavigate = { navigateTo(it) })
                }
                currentRoute == Screen.ImportPdf.route -> {
                    ImportPdfScreen(viewModel = viewModel, onBack = { navigateBack() })
                }
                currentRoute.startsWith("speaking_practice/") -> {
                    val lessonId = currentRoute.removePrefix("speaking_practice/").toIntOrNull() ?: 1
                    SpeakingPracticeScreen(
                        lessonId = lessonId,
                        viewModel = viewModel,
                        onBack = { navigateBack() }
                    )
                }
                currentRoute.startsWith("writing_practice/") -> {
                    val lessonId = currentRoute.removePrefix("writing_practice/").toIntOrNull() ?: 1
                    WritingPracticeScreen(
                        lessonId = lessonId,
                        viewModel = viewModel,
                        onBack = { navigateBack() }
                    )
                }
                currentRoute == "search" -> {
                    SearchScreen(viewModel = viewModel, onBack = { navigateBack() }, onNavigate = { navigateTo(it) })
                }
                else -> {
                    HomeScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                }
            }
        }
    }
}
