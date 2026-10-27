package com.example.data.repository

import android.content.Context
import com.example.data.dao.EducationDao
import com.example.data.database.AppDatabase
import com.example.data.database.DatabasePrepopulator
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray

class EducationRepository(
    private val dao: EducationDao
) {
    // Lessons
    val allLessons: Flow<List<LessonEntity>> = dao.getAllLessons()
    val completedLessonCount: Flow<Int> = dao.getCompletedLessonCount()

    suspend fun getLesson(lessonId: Int): LessonEntity? = dao.getLessonById(lessonId)

    suspend fun markLessonCompleted(lesson: LessonEntity, completed: Boolean) {
        dao.updateLesson(lesson.copy(isCompleted = completed))
    }

    suspend fun updateLessonBookmark(lesson: LessonEntity, bookmarked: Boolean) {
        dao.updateLesson(lesson.copy(isBookmarked = bookmarked))
    }

    suspend fun saveReadingPosition(lesson: LessonEntity, position: Int) {
        dao.updateLesson(lesson.copy(lastReadPosition = position))
    }

    fun getContentsForLesson(lessonId: Int): Flow<List<LessonContentEntity>> =
        dao.getContentsForLesson(lessonId)

    // Grammar
    val allGrammarRules: Flow<List<GrammarRuleEntity>> = dao.getAllGrammarRules()
    val unreviewedGrammar: Flow<List<GrammarRuleEntity>> = dao.getUnreviewedGrammarRules()

    suspend fun getGrammarRule(id: Long): GrammarRuleEntity? = dao.getGrammarRuleById(id)

    fun getGrammarRulesByLesson(lessonId: Int): Flow<List<GrammarRuleEntity>> =
        dao.getGrammarRulesByLesson(lessonId)

    fun getExamplesForRule(ruleId: Long): Flow<List<GrammarExampleEntity>> =
        dao.getExamplesForRule(ruleId)

    suspend fun toggleGrammarReviewed(rule: GrammarRuleEntity) {
        dao.updateGrammarRule(rule.copy(isReviewed = !rule.isReviewed))
    }

    suspend fun toggleGrammarFavorite(rule: GrammarRuleEntity) {
        val newFav = !rule.isFavorite
        dao.updateGrammarRule(rule.copy(isFavorite = newFav))
        if (newFav) {
            dao.addFavorite(
                FavoriteItemEntity(
                    itemType = "GRAMMAR",
                    itemId = rule.id,
                    title = rule.title,
                    subtitle = rule.titleAr
                )
            )
        } else {
            dao.removeFavorite("GRAMMAR", rule.id)
        }
    }

    // Vocabulary
    val allVocabulary: Flow<List<VocabularyEntity>> = dao.getAllVocabulary()

    fun getVocabularyByLesson(lessonId: Int): Flow<List<VocabularyEntity>> =
        dao.getVocabularyByLesson(lessonId)

    suspend fun toggleVocabularyFavorite(vocab: VocabularyEntity) {
        val newFav = !vocab.isFavorite
        dao.updateVocabulary(vocab.copy(isFavorite = newFav))
        if (newFav) {
            dao.addFavorite(
                FavoriteItemEntity(
                    itemType = "VOCABULARY",
                    itemId = vocab.id,
                    title = vocab.word,
                    subtitle = "${vocab.partOfSpeech} - ${vocab.meaningAr}"
                )
            )
        } else {
            dao.removeFavorite("VOCABULARY", vocab.id)
        }
    }

    // Questions
    val allQuestions: Flow<List<QuestionEntity>> = dao.getAllQuestions()
    val previouslyIncorrectQuestions: Flow<List<QuestionEntity>> = dao.getPreviouslyIncorrectQuestions()
    val totalAttemptsCount: Flow<Int> = dao.getTotalAttemptsCount()
    val correctAttemptsCount: Flow<Int> = dao.getCorrectAttemptsCount()

    fun getQuestionsByLesson(lessonId: Int): Flow<List<QuestionEntity>> =
        dao.getQuestionsByLesson(lessonId)

    suspend fun recordAttempt(questionId: Long, selected: String, isCorrect: Boolean, assessmentId: Long = 0) {
        dao.recordAttempt(
            QuestionAttemptEntity(
                questionId = questionId,
                selectedAnswer = selected,
                isCorrect = isCorrect,
                assessmentId = assessmentId
            )
        )
    }

    // Matching
    val allMatchingExercises: Flow<List<MatchingExerciseEntity>> = dao.getAllMatchingExercises()

    suspend fun getPairsForExercise(exerciseId: Long): List<MatchingPairEntity> =
        dao.getPairsForExercise(exerciseId)

    // Assessments
    val allAssessments: Flow<List<AssessmentEntity>> = dao.getAllAssessments()

    suspend fun saveAssessment(assessment: AssessmentEntity): Long =
        dao.insertAssessment(assessment)

    // Favorites
    val allFavorites: Flow<List<FavoriteItemEntity>> = dao.getAllFavorites()

    suspend fun removeFavorite(type: String, itemId: Long) {
        dao.removeFavorite(type, itemId)
    }

    // Global Search
    suspend fun searchAll(query: String): SearchResults {
        if (query.isBlank()) return SearchResults()
        return SearchResults(
            lessons = dao.searchLessons(query),
            grammar = dao.searchGrammar(query),
            vocabulary = dao.searchVocabulary(query),
            questions = dao.searchQuestions(query)
        )
    }

    // Course Counts
    suspend fun getCounts(): CourseCounts = withContext(Dispatchers.IO) {
        CourseCounts(
            lessons = dao.getLessonCount(),
            grammar = dao.getGrammarCount(),
            vocabulary = dao.getVocabularyCount(),
            questions = dao.getQuestionCount()
        )
    }

    // Reset Progress
    suspend fun resetProgressOnly() = withContext(Dispatchers.IO) {
        dao.clearAssessments()
        dao.clearQuestionAttempts()
        dao.resetLessonsProgress()
        dao.resetGrammarProgress()
    }

    // Import external JSON courseware
    suspend fun importJsonData(jsonString: String): ImportReport = withContext(Dispatchers.IO) {
        try {
            // Parses structured educational JSON
            val root = org.json.JSONObject(jsonString)
            val lessonsArray = root.optJSONArray("lessons")
            var importedCount = 0
            if (lessonsArray != null) {
                for (i in 0 until lessonsArray.length()) {
                    val obj = lessonsArray.getJSONObject(i)
                    val id = obj.optInt("id", (100 + i))
                    val lesson = LessonEntity(
                        id = id,
                        lessonNumber = obj.optInt("lessonNumber", id),
                        title = obj.optString("title", "Imported Lesson"),
                        titleAr = obj.optString("titleAr", ""),
                        description = obj.optString("description", ""),
                        sourceFilename = obj.optString("sourceFilename", "imported.pdf"),
                        pageCount = obj.optInt("pageCount", 1),
                        contentStatus = "IMPORTED_JSON"
                    )
                    dao.insertLessons(listOf(lesson))
                    importedCount++
                }
            }
            ImportReport(
                success = true,
                message = "Successfully imported $importedCount additional lessons.",
                itemsImported = importedCount
            )
        } catch (e: Exception) {
            ImportReport(
                success = false,
                message = "Import failed: ${e.localizedMessage ?: "Invalid JSON format"}",
                itemsImported = 0
            )
        }
    }
}

data class SearchResults(
    val lessons: List<LessonEntity> = emptyList(),
    val grammar: List<GrammarRuleEntity> = emptyList(),
    val vocabulary: List<VocabularyEntity> = emptyList(),
    val questions: List<QuestionEntity> = emptyList()
)

data class CourseCounts(
    val lessons: Int = 11,
    val grammar: Int = 8,
    val vocabulary: Int = 25,
    val questions: Int = 20
)

data class ImportReport(
    val success: Boolean,
    val message: String,
    val itemsImported: Int
)
