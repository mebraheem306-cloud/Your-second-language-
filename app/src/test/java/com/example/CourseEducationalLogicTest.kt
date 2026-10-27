package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseEducationalLogicTest {

    @Test
    fun testAssessmentPercentageCalculation() {
        val score = 8
        val total = 10
        val percentage = ((score.toFloat() / total.toFloat()) * 100).toInt()
        assertEquals(80, percentage)
    }

    @Test
    fun testMatchingPairVerification() {
        val correctPairs = mapOf(
            "Could you help me?" to "Requesting help",
            "May I borrow…?" to "Asking permission to take something",
            "Would you mind…?" to "Asking politely if something is okay"
        )

        val userSelections = mapOf(
            "Could you help me?" to "Requesting help",
            "May I borrow…?" to "Asking permission to take something",
            "Would you mind…?" to "Wrong answer"
        )

        val correctCount = correctPairs.count { (left, right) ->
            userSelections[left] == right
        }

        assertEquals(2, correctCount)
    }

    @Test
    fun testPoliteRequestDistinction() {
        val expression = "Would you mind closing the window?"
        val isPolite = expression.startsWith("Would you mind") || expression.startsWith("Could you")
        assertTrue(isPolite)
    }
}
