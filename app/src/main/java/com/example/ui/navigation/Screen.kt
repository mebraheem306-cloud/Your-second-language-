package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Lessons : Screen("lessons")
    object LessonDetail : Screen("lesson_detail/{lessonId}") {
        fun createRoute(lessonId: Int) = "lesson_detail/$lessonId"
    }
    object Grammar : Screen("grammar")
    object GrammarDetail : Screen("grammar_detail/{ruleId}") {
        fun createRoute(ruleId: Long) = "grammar_detail/$ruleId"
    }
    object Questions : Screen("questions")
    object Matching : Screen("matching")
    object Vocabulary : Screen("vocabulary")
    object Assessment : Screen("assessment")
    object Progress : Screen("progress")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
    object ImportPdf : Screen("import_pdf")
    object SpeakingPractice : Screen("speaking_practice/{lessonId}") {
        fun createRoute(lessonId: Int) = "speaking_practice/$lessonId"
    }
    object WritingPractice : Screen("writing_practice/{lessonId}") {
        fun createRoute(lessonId: Int) = "writing_practice/$lessonId"
    }
}
