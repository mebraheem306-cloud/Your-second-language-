package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = EducationRepository(database.educationDao())

    // Lessons
    val lessons: StateFlow<List<LessonEntity>> = repository.allLessons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedLessonsCount: StateFlow<Int> = repository.completedLessonCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Grammar
    val grammarRules: StateFlow<List<GrammarRuleEntity>> = repository.allGrammarRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreviewedGrammar: StateFlow<List<GrammarRuleEntity>> = repository.unreviewedGrammar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Vocabulary
    val vocabulary: StateFlow<List<VocabularyEntity>> = repository.allVocabulary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Questions
    val questions: StateFlow<List<QuestionEntity>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incorrectQuestions: StateFlow<List<QuestionEntity>> = repository.previouslyIncorrectQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalAttempts: StateFlow<Int> = repository.totalAttemptsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val correctAttempts: StateFlow<Int> = repository.correctAttemptsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Matching
    val matchingExercises: StateFlow<List<MatchingExerciseEntity>> = repository.allMatchingExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Assessments
    val assessments: StateFlow<List<AssessmentEntity>> = repository.allAssessments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorites
    val favorites: StateFlow<List<FavoriteItemEntity>> = repository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard counts
    private val _counts = MutableStateFlow(CourseCounts())
    val counts: StateFlow<CourseCounts> = _counts.asStateFlow()

    // Global Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow(SearchResults())
    val searchResults: StateFlow<SearchResults> = _searchResults.asStateFlow()

    // Settings
    private val _showArabic = MutableStateFlow(true)
    val showArabic: StateFlow<Boolean> = _showArabic.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        refreshCounts()
    }

    fun refreshCounts() {
        viewModelScope.launch {
            _counts.value = repository.getCounts()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _searchResults.value = repository.searchAll(query)
        }
    }

    fun toggleArabic(enabled: Boolean) {
        _showArabic.value = enabled
    }

    fun toggleTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    fun toggleLessonCompleted(lesson: LessonEntity) {
        viewModelScope.launch {
            repository.markLessonCompleted(lesson, !lesson.isCompleted)
        }
    }

    fun toggleLessonBookmark(lesson: LessonEntity) {
        viewModelScope.launch {
            repository.updateLessonBookmark(lesson, !lesson.isBookmarked)
        }
    }

    fun toggleGrammarFavorite(rule: GrammarRuleEntity) {
        viewModelScope.launch {
            repository.toggleGrammarFavorite(rule)
        }
    }

    fun toggleGrammarReviewed(rule: GrammarRuleEntity) {
        viewModelScope.launch {
            repository.toggleGrammarReviewed(rule)
        }
    }

    fun toggleVocabFavorite(vocab: VocabularyEntity) {
        viewModelScope.launch {
            repository.toggleVocabularyFavorite(vocab)
        }
    }

    fun removeFavorite(type: String, itemId: Long) {
        viewModelScope.launch {
            repository.removeFavorite(type, itemId)
        }
    }

    fun recordQuestionAttempt(questionId: Long, selectedAnswer: String, isCorrect: Boolean, assessmentId: Long = 0) {
        viewModelScope.launch {
            repository.recordAttempt(questionId, selectedAnswer, isCorrect, assessmentId)
        }
    }

    fun saveAssessmentResult(assessment: AssessmentEntity) {
        viewModelScope.launch {
            repository.saveAssessment(assessment)
            refreshCounts()
        }
    }

    fun resetProgress(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.resetProgressOnly()
            refreshCounts()
            onComplete()
        }
    }
}
