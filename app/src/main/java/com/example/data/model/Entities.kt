package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: Int,
    val lessonNumber: Int,
    val title: String,
    val titleAr: String,
    val description: String,
    val sourceFilename: String,
    val pageCount: Int,
    val contentStatus: String = "VERIFIED_PDF", // VERIFIED_PDF or IMPORTED
    val estimatedMinutes: Int = 15,
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false,
    val lastReadPosition: Int = 0
)

@Entity(tableName = "lesson_contents")
data class LessonContentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: Int,
    val contentType: String, // OBJECTIVES, EXPRESSIONS, READING, DIALOGUE, LISTENING, WRITING, GRAMMAR, EXERCISE
    val title: String,
    val contentText: String,
    val sequenceNumber: Int,
    val sourcePage: Int,
    val sourceMetadata: String = "Original PDF Courseware (Level Two)",
    val isOriginalPdf: Boolean = true
)

@Entity(tableName = "grammar_rules")
data class GrammarRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val titleAr: String,
    val explanationEn: String,
    val explanationAr: String,
    val usage: String,
    val usageAr: String,
    val structure: String,
    val affirmativeForm: String,
    val negativeForm: String,
    val questionForm: String,
    val commonMistakes: String,
    val sourceLessonId: Int,
    val sourcePage: Int,
    val verificationStatus: String = "VERIFIED_FROM_PDF",
    val isFavorite: Boolean = false,
    val isReviewed: Boolean = false
)

@Entity(tableName = "grammar_examples")
data class GrammarExampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val grammarRuleId: Long,
    val exampleText: String,
    val exampleTranslationAr: String,
    val isOriginalSourceExample: Boolean = true
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: Int,
    val questionType: String, // MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, POLITE_IDENTIFY, ERROR_CORRECTION, ORDERING
    val questionText: String,
    val questionTextAr: String = "",
    val optionsJson: String, // JSON array of string options
    val correctAnswer: String,
    val explanationEn: String,
    val explanationAr: String,
    val grammarTopic: String = "",
    val vocabularyTopic: String = "",
    val sourcePage: Int,
    val verificationStatus: String = "VERIFIED_FROM_PDF",
    val isFavorite: Boolean = false
)

@Entity(tableName = "matching_exercises")
data class MatchingExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: Int,
    val title: String,
    val category: String, // EXPRESSION_MEANING, VOCAB_DEFINITION, PHRASE_PURPOSE, PROBLEM_ADVICE
    val sourcePage: Int
)

@Entity(tableName = "matching_pairs")
data class MatchingPairEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long,
    val leftItem: String,
    val rightItem: String,
    val sequenceNumber: Int
)

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val meaningAr: String,
    val partOfSpeech: String,
    val definitionEn: String,
    val exampleSentence: String,
    val sourceLessonId: Int,
    val sourcePage: Int,
    val isFavorite: Boolean = false,
    val isMastered: Boolean = false
)

@Entity(tableName = "user_assessments")
data class AssessmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val assessmentType: String, // QUICK_QUIZ, GRAMMAR_FOCUS, VOCAB_FOCUS, LESSON_TEST, COURSE_FINAL, INCORRECT_REVIEW
    val timestamp: Long = System.currentTimeMillis(),
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val grammarAccuracy: Int = 0,
    val vocabAccuracy: Int = 0,
    val recommendedLessonsJson: String = "[]",
    val recommendedGrammarJson: String = "[]"
)

@Entity(tableName = "question_attempts")
data class QuestionAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: Long,
    val assessmentId: Long = 0, // 0 if study mode
    val selectedAnswer: String,
    val isCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemType: String, // LESSON, GRAMMAR, VOCABULARY, QUESTION
    val itemId: Long,
    val title: String,
    val subtitle: String,
    val timestamp: Long = System.currentTimeMillis()
)
