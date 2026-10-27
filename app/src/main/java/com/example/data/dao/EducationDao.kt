package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EducationDao {

    // Lessons
    @Query("SELECT * FROM lessons ORDER BY lessonNumber ASC")
    fun getAllLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId LIMIT 1")
    suspend fun getLessonById(lessonId: Int): LessonEntity?

    @Query("SELECT COUNT(*) FROM lessons")
    suspend fun getLessonCount(): Int

    @Query("SELECT COUNT(*) FROM lessons WHERE isCompleted = 1")
    fun getCompletedLessonCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    // Lesson Content
    @Query("SELECT * FROM lesson_contents WHERE lessonId = :lessonId ORDER BY sequenceNumber ASC")
    fun getContentsForLesson(lessonId: Int): Flow<List<LessonContentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessonContents(contents: List<LessonContentEntity>)

    // Grammar Rules
    @Query("SELECT * FROM grammar_rules ORDER BY sourceLessonId ASC, id ASC")
    fun getAllGrammarRules(): Flow<List<GrammarRuleEntity>>

    @Query("SELECT * FROM grammar_rules WHERE id = :id LIMIT 1")
    suspend fun getGrammarRuleById(id: Long): GrammarRuleEntity?

    @Query("SELECT * FROM grammar_rules WHERE sourceLessonId = :lessonId")
    fun getGrammarRulesByLesson(lessonId: Int): Flow<List<GrammarRuleEntity>>

    @Query("SELECT * FROM grammar_rules WHERE isReviewed = 0")
    fun getUnreviewedGrammarRules(): Flow<List<GrammarRuleEntity>>

    @Query("SELECT COUNT(*) FROM grammar_rules")
    suspend fun getGrammarCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrammarRules(rules: List<GrammarRuleEntity>): List<Long>

    @Update
    suspend fun updateGrammarRule(rule: GrammarRuleEntity)

    // Grammar Examples
    @Query("SELECT * FROM grammar_examples WHERE grammarRuleId = :ruleId")
    fun getExamplesForRule(ruleId: Long): Flow<List<GrammarExampleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrammarExamples(examples: List<GrammarExampleEntity>)

    // Questions
    @Query("SELECT * FROM questions ORDER BY lessonId ASC, id ASC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE lessonId = :lessonId")
    fun getQuestionsByLesson(lessonId: Int): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE grammarTopic = :grammarTopic")
    fun getQuestionsByGrammarTopic(grammarTopic: String): Flow<List<QuestionEntity>>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    // Matching Exercises
    @Query("SELECT * FROM matching_exercises ORDER BY lessonId ASC")
    fun getAllMatchingExercises(): Flow<List<MatchingExerciseEntity>>

    @Query("SELECT * FROM matching_pairs WHERE exerciseId = :exerciseId ORDER BY sequenceNumber ASC")
    suspend fun getPairsForExercise(exerciseId: Long): List<MatchingPairEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchingExercises(exercises: List<MatchingExerciseEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchingPairs(pairs: List<MatchingPairEntity>)

    // Vocabulary
    @Query("SELECT * FROM vocabulary ORDER BY word ASC")
    fun getAllVocabulary(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE sourceLessonId = :lessonId ORDER BY word ASC")
    fun getVocabularyByLesson(lessonId: Int): Flow<List<VocabularyEntity>>

    @Query("SELECT COUNT(*) FROM vocabulary")
    suspend fun getVocabularyCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(vocab: List<VocabularyEntity>)

    @Update
    suspend fun updateVocabulary(vocab: VocabularyEntity)

    // Assessments & Attempts
    @Query("SELECT * FROM user_assessments ORDER BY timestamp DESC")
    fun getAllAssessments(): Flow<List<AssessmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordAttempt(attempt: QuestionAttemptEntity)

    @Query("SELECT * FROM question_attempts WHERE isCorrect = 0 ORDER BY timestamp DESC")
    fun getIncorrectAttempts(): Flow<List<QuestionAttemptEntity>>

    @Query("SELECT * FROM questions WHERE id IN (SELECT DISTINCT questionId FROM question_attempts WHERE isCorrect = 0)")
    fun getPreviouslyIncorrectQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT COUNT(*) FROM question_attempts")
    fun getTotalAttemptsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM question_attempts WHERE isCorrect = 1")
    fun getCorrectAttemptsCount(): Flow<Int>

    // Favorites
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteItemEntity)

    @Query("DELETE FROM favorites WHERE itemType = :type AND itemId = :itemId")
    suspend fun removeFavorite(type: String, itemId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE itemType = :type AND itemId = :itemId)")
    fun isFavorite(type: String, itemId: Long): Flow<Boolean>

    // Global Search
    @Query("""
        SELECT * FROM lessons 
        WHERE title LIKE '%' || :query || '%' OR titleAr LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'
    """)
    suspend fun searchLessons(query: String): List<LessonEntity>

    @Query("""
        SELECT * FROM grammar_rules 
        WHERE title LIKE '%' || :query || '%' OR titleAr LIKE '%' || :query || '%' 
        OR explanationEn LIKE '%' || :query || '%' OR explanationAr LIKE '%' || :query || '%'
    """)
    suspend fun searchGrammar(query: String): List<GrammarRuleEntity>

    @Query("""
        SELECT * FROM vocabulary 
        WHERE word LIKE '%' || :query || '%' OR meaningAr LIKE '%' || :query || '%' OR definitionEn LIKE '%' || :query || '%'
    """)
    suspend fun searchVocabulary(query: String): List<VocabularyEntity>

    @Query("""
        SELECT * FROM questions 
        WHERE questionText LIKE '%' || :query || '%' OR explanationEn LIKE '%' || :query || '%'
    """)
    suspend fun searchQuestions(query: String): List<QuestionEntity>

    // Progress reset
    @Query("DELETE FROM user_assessments")
    suspend fun clearAssessments()

    @Query("DELETE FROM question_attempts")
    suspend fun clearQuestionAttempts()

    @Query("UPDATE lessons SET isCompleted = 0, lastReadPosition = 0")
    suspend fun resetLessonsProgress()

    @Query("UPDATE grammar_rules SET isReviewed = 0")
    suspend fun resetGrammarProgress()
}
