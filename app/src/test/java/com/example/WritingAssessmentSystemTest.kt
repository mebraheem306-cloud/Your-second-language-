package com.example

import com.example.data.ai.GeminiAIService
import com.example.data.ai.TaskStatus
import com.example.data.ai.WritingTaskRegistry
import com.example.data.ai.WritingTaskSpecification
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WritingAssessmentSystemTest {

    @Before
    fun setUp() {
        WritingTaskRegistry.resetToDefaults()
    }

    private val l1Prompt get() = WritingTaskRegistry.getSpecificationForLesson(1).promptText
    private val l2Prompt get() = WritingTaskRegistry.getSpecificationForLesson(2).promptText

    // ========================================================
    // PART 14 & 15: AUTOMATED REGRESSION & UNIT TESTS
    // ========================================================

    @Test
    fun test01_EmptySubmission_ExpectsEmptyStatusAndZeroScore() {
        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l1Prompt,
            studentText = "   \n\t   ",
            topic = "Polite Requests",
            lessonNumber = 1
        )

        assertEquals("Status must be EMPTY", TaskStatus.EMPTY, result.taskStatus)
        assertEquals("Total score must be 0", 0, result.score)
        assertEquals("Task completion score must be 0", 0, result.taskCompletionScore)
        assertEquals("Completed items must be 0", 0, result.completedItems)
    }

    @Test
    fun test02_OneWordSubmissionGood_ExpectsPartialZeroItemsAndScoreBetweenZeroAndFive() {
        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l1Prompt,
            studentText = "good",
            topic = "Polite Requests",
            lessonNumber = 1
        )

        assertEquals("Completed items must be 0 for 'good'", 0, result.completedItems)
        assertEquals("Task completion score must be 0/30", 0, result.taskCompletionScore)
        assertTrue("Score must be between 0 and 5 (got ${result.score})", result.score in 0..5)
        assertEquals("Task status should be PARTIAL", TaskStatus.PARTIAL, result.taskStatus)
        assertEquals("Organization score must be 0", 0, result.organizationScore)
        assertEquals("Style score must be 0", 0, result.styleScore)
    }

    @Test
    fun test02b_ShortPhraseHiBro_ExpectsPartialZeroItemsAndScoreBetweenZeroAndFive() {
        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l1Prompt,
            studentText = "hi bro",
            topic = "Polite Requests",
            lessonNumber = 1
        )

        assertEquals("Completed items must be 0 for 'hi bro'", 0, result.completedItems)
        assertEquals("Task completion score must be 0/30", 0, result.taskCompletionScore)
        assertTrue("Score must be between 0 and 5 (got ${result.score})", result.score in 0..5)
        assertEquals("Task status should be PARTIAL", TaskStatus.PARTIAL, result.taskStatus)
    }

    @Test
    fun test03_IrrelevantSubmissionFootball_ExpectsIrrelevantStatusAndLowScore() {
        val submission = """
            I love playing football with my team every Saturday.
            The goalkeeper made an amazing save and we won the tournament yesterday.
            Football is the most exciting sport in the world.
        """.trimIndent()

        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l1Prompt,
            studentText = submission,
            topic = "Polite Requests",
            lessonNumber = 1
        )

        assertEquals("Status must be IRRELEVANT", TaskStatus.IRRELEVANT, result.taskStatus)
        assertEquals("Task completion score must be 0", 0, result.taskCompletionScore)
        assertTrue("Score must be <= 15 (got ${result.score})", result.score <= 15)
    }

    @Test
    fun test04_CopiedInstructionsVerbatim_ExpectsCopiedInstructionsAndScoreBetweenZeroAndTwenty() {
        val submission = """
            Ask your teacher to explain the lesson again.
            Ask your friend to help you with homework.
            Ask someone to open the window.
            Ask to borrow a phone.
        """.trimIndent()

        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l1Prompt,
            studentText = submission,
            topic = "Polite Requests",
            lessonNumber = 1
        )

        assertEquals("Status must be COPIED_INSTRUCTIONS", TaskStatus.COPIED_INSTRUCTIONS, result.taskStatus)
        assertTrue("copiedInstructions flag must be true", result.copiedInstructions)
        assertTrue("Score must normally be between 0 and 20 (got ${result.score})", result.score in 0..20)
        assertTrue("Task completion score must be <= 5", result.taskCompletionScore <= 5)
        assertTrue("Feedback must mention copying instructions", result.feedbackEn.contains("copied", ignoreCase = true))
    }

    @Test
    fun test05_PartiallyCorrectTwoPoliteRequests_ExpectsPartialCredit() {
        val submission = """
            1. Could you please explain the lesson again?
            2. Would you mind opening the window?
        """.trimIndent()

        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l1Prompt,
            studentText = submission,
            topic = "Polite Requests",
            lessonNumber = 1
        )

        assertEquals("Status must be PARTIAL", TaskStatus.PARTIAL, result.taskStatus)
        assertEquals("Completed items must be 2", 2, result.completedItems)
        assertTrue("Score must be proportional between 25 and 70 (got ${result.score})", result.score in 25..70)
        assertTrue("Task completion should be 15 (got ${result.taskCompletionScore})", result.taskCompletionScore in 12..18)
    }

    @Test
    fun test06_FullyCorrectFourPoliteRequests_ExpectsCompleteStatusAndHighScore() {
        val submission = """
            1. Could you please explain the lesson again?
            2. Could you help me with my homework, please?
            3. Would you mind opening the window?
            4. May I borrow your phone, please?
        """.trimIndent()

        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l1Prompt,
            studentText = submission,
            topic = "Polite Requests",
            lessonNumber = 1
        )

        assertEquals("Status must be COMPLETE", TaskStatus.COMPLETE, result.taskStatus)
        assertEquals("Completed items must be 4", 4, result.completedItems)
        assertTrue("Score must be >= 75 (got ${result.score})", result.score >= 75)
        assertTrue("Task completion score must be >= 25", result.taskCompletionScore >= 25)
    }

    @Test
    fun test07_CorrectAlternativeWording_ExpectsCompleteStatusAndHighScore() {
        val submission = """
            1. Would you be so kind as to explain the lesson one more time, teacher?
            2. Can you please give me a hand with this homework assignment?
            3. Would it be possible for you to open the window, please?
            4. Could I please use your mobile phone for a minute?
        """.trimIndent()

        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l1Prompt,
            studentText = submission,
            topic = "Polite Requests",
            lessonNumber = 1
        )

        assertEquals("Status must be COMPLETE", TaskStatus.COMPLETE, result.taskStatus)
        assertEquals("Completed items must be 4", 4, result.completedItems)
        assertTrue("Score must be >= 75 (got ${result.score})", result.score >= 75)
    }

    @Test
    fun test08_GrammaticallyImperfectButRelevantPoliteRequests_AwardsSubstantialTaskCredit() {
        val submission = """
            1. Could you explain lesson please again teacher?
            2. Could you help me with homework my friend?
            3. Would you mind open the window please?
            4. May I borrow phone from you?
        """.trimIndent()

        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l1Prompt,
            studentText = submission,
            topic = "Polite Requests",
            lessonNumber = 1
        )

        // Communicatively successful requests with minor grammatical infelicities
        assertEquals("Status must be COMPLETE as communicative intent is met", TaskStatus.COMPLETE, result.taskStatus)
        assertEquals("All 4 items should be counted", 4, result.completedItems)
        assertTrue("Task completion score must be high (>= 22)", result.taskCompletionScore >= 22)
    }

    @Test
    fun test09_TaskMismatch_Lesson1InstructionsSubmittedIntoLesson2_ExpectsTaskMismatchStatusAndLowScore() {
        val l1Instructions = """
            Write polite requests for these situations:
            1. Ask your teacher to explain the lesson again.
            2. Ask your friend to help you with homework.
            3. Ask someone to open the window.
            4. Ask to borrow a phone.
        """.trimIndent()

        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l2Prompt,
            studentText = l1Instructions,
            topic = "Giving Advice",
            lessonNumber = 2
        )

        assertEquals("Status must be TASK_MISMATCH", TaskStatus.TASK_MISMATCH, result.taskStatus)
        assertEquals("Completed items must be 0", 0, result.completedItems)
        assertEquals("Task completion score must be 0", 0, result.taskCompletionScore)
        assertTrue("Overall score must be strictly <= 10 (got ${result.score})", result.score <= 10)
        assertTrue("Feedback must mention task mismatch or Lesson 1", result.feedbackEn.contains("Lesson 1", ignoreCase = true) || result.feedbackEn.contains("mismatch", ignoreCase = true))
    }

    @Test
    fun test10_Lesson2AdviceWriting_MissingOughtTo_AwardsPartialCredit() {
        val submission = """
            You should study English every day for thirty minutes.
            You should practice speaking with native speakers online.
            You shouldn't worry about making mistakes while learning.
            You should also read short English news articles regularly.
        """.trimIndent()

        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l2Prompt,
            studentText = submission,
            topic = "Giving Advice",
            lessonNumber = 2
        )

        assertEquals("Status must be PARTIAL because 'ought to' is missing", TaskStatus.PARTIAL, result.taskStatus)
        assertTrue("Should award partial task completion (between 12 and 25, got ${result.taskCompletionScore})", result.taskCompletionScore in 12..25)
        assertTrue("Score should reflect partial credit (between 40 and 74, got ${result.score})", result.score in 40..74)
        assertTrue("Feedback should point out missing 'ought to'", result.mistakes.any { it.contains("ought to", ignoreCase = true) })
    }

    @Test
    fun test10b_Lesson2AdviceWriting_CompleteWithAllStructures_AwardsFullCredit() {
        val submission = """
            You should practice speaking English every day with your friends.
            You shouldn't be afraid of making mistakes because that is how you learn.
            Furthermore, you ought to read English stories and news articles regularly.
            You should also listen to English podcasts to improve your listening comprehension.
        """.trimIndent()

        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l2Prompt,
            studentText = submission,
            topic = "Giving Advice",
            lessonNumber = 2
        )

        assertEquals("Status must be COMPLETE", TaskStatus.COMPLETE, result.taskStatus)
        assertTrue("Task completion score must be high (>= 25)", result.taskCompletionScore >= 25)
        assertTrue("Score should be high (>= 75, got ${result.score})", result.score >= 75)
    }

    @Test
    fun test11_MissingScoreFieldsOrMalformedJson_ParserHandlesGracefully() {
        val malformedJson = """
            {
              "task_type": "WRITING",
              "task_status": "COMPLETE",
              "feedback_en": "Good attempt"
            }
        """.trimIndent()

        val parsed = GeminiAIService.parseEvaluationJson(malformedJson, isSpeaking = false)

        assertNotNull(parsed)
        assertEquals("Missing task_completion_score defaults to 0", 0, parsed.taskCompletionScore)
        assertEquals("Missing grammar_score defaults to 0", 0, parsed.grammarScore)
        assertEquals("Total score defaults to 0 without fabricated points", 0, parsed.score)
    }

    @Test
    fun test12_InconsistentOverallScoreFromAI_RecalculatedFromValidatedComponents() {
        // AI proposed an inflated overall_score of 99, but criterion sum is only 40
        val fakeAiResult = com.example.data.ai.EvaluationResult(
            taskType = "POLITE_REQUESTS",
            taskStatus = TaskStatus.PARTIAL,
            completedItems = 2,
            totalRequiredItems = 4,
            taskCompletionScore = 15,
            grammarScore = 10,
            vocabularyScore = 8,
            organizationScore = 5,
            punctuationScore = 4,
            styleScore = 6,
            score = 99 // Inconsistent total
        )

        val submission = "1. Could you explain the lesson?\n2. Would you open the window?"
        val validated = GeminiAIService.sanitizeAndEnforceScoreRules(fakeAiResult, l1Prompt, submission, lessonNumber = 1)

        val expectedSum = 15 + 10 + 8 + 0 + 4 + 6 // org is 0 because word count < 10
        assertNotEquals("Inconsistent AI total 99 must be rejected", 99, validated.score)
        assertTrue("Recalculated score must match component sum bounds", validated.score in 35..50)
    }

    @Test
    fun test13_ScoreDisplayFormatting_GuaranteesNoReversalInRTL() {
        val formatted = GeminiAIService.formatScoreDisplay(44, 100)

        assertTrue("Must start with LTR mark", formatted.startsWith("\u200E"))
        assertTrue("Must end with LTR mark", formatted.endsWith("\u200E"))
        assertTrue("Must contain '44 / 100'", formatted.contains("44 / 100"))
        assertFalse("Must NOT contain reversed '100 / 44'", formatted.contains("100 / 44"))

        val rubricFormatted = GeminiAIService.formatRubricScore(5, 30)
        assertTrue("Rubric must contain '5/30'", rubricFormatted.contains("5/30"))
        assertFalse("Rubric must NOT contain reversed '30/5'", rubricFormatted.contains("30/5"))
    }

    @Test
    fun test14_LegitimateTranscriptionAssignment_EvaluatesAccurately() {
        val transcriptionPrompt = "Transcribe and copy the passage below accurately: 'The quick brown fox jumps over the lazy dog.'"
        val studentCopy = "The quick brown fox jumps over the lazy dog."

        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = transcriptionPrompt,
            studentText = studentCopy,
            topic = "Transcription",
            lessonNumber = 1
        )

        assertEquals("Legitimate transcription should be COMPLETE", TaskStatus.COMPLETE, result.taskStatus)
        assertFalse("Should not be penalized as copied instructions", result.copiedInstructions)
        assertTrue("Score should be high for correct transcription (>= 80)", result.score >= 80)
    }

    @Test
    fun test15_AllElevenLessonsRegisteredWithTaskSpecifications() {
        val allTasks = WritingTaskRegistry.getAllTasks()

        assertEquals("Must contain all 11 lesson task specifications", 11, allTasks.size)
        for (i in 1..11) {
            val spec = WritingTaskRegistry.getSpecificationForLesson(i)
            assertEquals("Lesson number must match", i, spec.lessonNumber)
            assertTrue("Prompt must not be empty", spec.promptText.isNotBlank())
            assertTrue("Target grammar must not be empty", spec.targetGrammar.isNotBlank())
            assertTrue("Task type must not be empty", spec.taskType.isNotBlank())
            assertTrue("Sample model must not be empty", spec.referenceSample.isNotBlank())
        }
    }

    @Test
    fun test16_BilingualFeedbackDeliveredInArabicAndEnglish() {
        val result = GeminiAIService.fallbackWritingEvaluation(
            writingPrompt = l1Prompt,
            studentText = "good",
            topic = "Polite Requests",
            lessonNumber = 1
        )

        assertTrue("English feedback must be present", result.feedbackEn.isNotBlank())
        assertTrue("Arabic feedback must be present", result.feedbackAr.isNotBlank())
        assertTrue("Arabic feedback must contain Arabic script", result.feedbackAr.any { it in '\u0600'..'\u06FF' })
    }

    @Test
    fun test17_NewWritingActivityRegisteredInEngine_WorksSeamlessly() {
        val customSpec = WritingTaskSpecification(
            lessonId = 99,
            lessonNumber = 99,
            activityId = "L99_CUSTOM_OPINION",
            lessonTitle = "Opinion Essay",
            promptText = "Write your opinion on remote work using: In my opinion, Furthermore, In conclusion.",
            taskType = "OPINION_ESSAY",
            targetGrammar = "Opinion Expressions",
            targetVocabulary = "Remote work, productivity, office, balance",
            expectedResponseFormat = "An opinion essay using opinion expressions.",
            requiredItemCount = 3,
            minWords = 25,
            referenceSample = "In my opinion, remote work provides great flexibility. Furthermore, it saves daily commuting time. In conclusion, hybrid work is the best approach."
        )

        WritingTaskRegistry.registerTask(customSpec)
        val retrieved = WritingTaskRegistry.getSpecificationForLesson(99)

        assertEquals("Custom task registered successfully", "L99_CUSTOM_OPINION", retrieved.activityId)
        val prompt = GeminiAIService.buildWritingEvaluationPrompt(
            lessonTitle = retrieved.lessonTitle,
            writingPrompt = retrieved.promptText,
            studentText = "In my opinion, it is great.",
            targetGrammarTopic = retrieved.targetGrammar,
            lessonNumber = 99,
            activityId = retrieved.activityId
        )

        assertTrue(prompt.contains("L99_CUSTOM_OPINION"))
        assertTrue(prompt.contains("OPINION_ESSAY"))
    }
}
