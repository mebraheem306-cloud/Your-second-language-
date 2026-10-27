package com.example.data.ai

/**
 * Complete specification of a writing activity, ensuring every lesson
 * has explicit criteria, target grammar, expected response format,
 * and reference models for accurate, unbiased AI assessment.
 */
data class WritingTaskSpecification(
    val lessonId: Int,
    val lessonNumber: Int,
    val activityId: String,
    val lessonTitle: String,
    val promptText: String,
    val taskType: String,
    val targetGrammar: String,
    val targetVocabulary: String,
    val expectedResponseFormat: String,
    val requiredItemCount: Int = 1,
    val minWords: Int = 15,
    val maxWords: Int = 250,
    val referenceAnswers: List<String> = emptyList(),
    val referenceSample: String = "",
    val requiredKeywords: List<String> = emptyList(),
    val requiredStructures: List<String> = emptyList()
)

object WritingTaskRegistry {

    private val tasks = mutableMapOf<Int, WritingTaskSpecification>()

    init {
        registerBuiltInTasks()
    }

    private fun registerBuiltInTasks() {
        // Lesson 1: Making Requests
        registerTask(
            WritingTaskSpecification(
                lessonId = 1,
                lessonNumber = 1,
                activityId = "L1_POLITE_REQUESTS",
                lessonTitle = "Making Requests",
                promptText = "Write polite requests for these situations:\n1. Ask your teacher to explain the lesson again.\n2. Ask your friend to help you with homework.\n3. Ask someone to open the window.\n4. Ask to borrow a phone.",
                taskType = "POLITE_REQUESTS",
                targetGrammar = "Polite Requests (Could you, Would you mind, May I, Can you please)",
                targetVocabulary = "Teacher, explain, lesson, friend, help, homework, open, window, borrow, phone",
                expectedResponseFormat = "Four distinct polite requests, each addressing one of the assigned situations.",
                requiredItemCount = 4,
                minWords = 16,
                maxWords = 80,
                referenceAnswers = listOf(
                    "Item 1: Could you please explain the lesson again?",
                    "Item 2: Could you help me with my homework, please?",
                    "Item 3: Would you mind opening the window?",
                    "Item 4: May I borrow your phone, please?"
                ),
                referenceSample = "1. Could you please explain the lesson again?\n2. Could you help me with my homework, please?\n3. Would you mind opening the window?\n4. May I borrow your phone, please?",
                requiredStructures = listOf("could you", "would you mind", "may i", "can you please", "would you please")
            )
        )

        // Lesson 2: Giving Advice
        registerTask(
            WritingTaskSpecification(
                lessonId = 2,
                lessonNumber = 2,
                activityId = "L2_GIVING_ADVICE",
                lessonTitle = "Giving Advice",
                promptText = "Write a short paragraph (4–5 sentences) about 'Advice for a student who wants to improve English' using should, shouldn't, and ought to.",
                taskType = "ADVICE_WRITING",
                targetGrammar = "Giving Advice (should, shouldn't, ought to)",
                targetVocabulary = "Study, practice, improve, language, habits, read, listen, mistakes, English",
                expectedResponseFormat = "A coherent advice paragraph containing 4 to 5 sentences incorporating should, shouldn't, and ought to.",
                requiredItemCount = 4,
                minWords = 25,
                maxWords = 120,
                referenceAnswers = listOf(
                    "Sentence 1: You should practice speaking English every day.",
                    "Sentence 2: You shouldn't be afraid of making mistakes.",
                    "Sentence 3: You ought to read English stories and news regularly.",
                    "Sentence 4: You should also listen to English podcasts during your free time."
                ),
                referenceSample = "You should practice speaking English every day with your friends. You shouldn't be afraid of making mistakes because that is how you learn. Furthermore, you ought to read English stories and articles regularly to expand your vocabulary. You should also listen to English podcasts or songs to improve your pronunciation.",
                requiredStructures = listOf("should", "shouldn't", "ought to")
            )
        )

        // Lesson 3: Making Suggestions
        registerTask(
            WritingTaskSpecification(
                lessonId = 3,
                lessonNumber = 3,
                activityId = "L3_MAKING_SUGGESTIONS",
                lessonTitle = "Making Suggestions",
                promptText = "Write a short paragraph (10–15 sentences) on the topic: 'What should we do this weekend?' using suggestion structures (Let's, Why don't we, How about + ing).",
                taskType = "SUGGESTIONS_WRITING",
                targetGrammar = "Suggestion Structures (Let's, Why don't we, How about + -ing, We could)",
                targetVocabulary = "Weekend, plans, park, cinema, dinner, friends, relax, museum",
                expectedResponseFormat = "A paragraph outlining weekend plans using suggestion formulas.",
                requiredItemCount = 8,
                minWords = 40,
                maxWords = 200,
                referenceSample = "The weekend is approaching and we need to make exciting plans. First, let's visit the city art museum on Saturday morning. Why don't we have lunch at the new Italian restaurant downtown? How about going for a walk along the river in the afternoon? We could also invite our friends to join us for a movie in the evening. On Sunday, let's sleep in and have a relaxed breakfast. How about riding bicycles in the park if the weather is sunny? Why don't we cook a homemade dinner together afterwards? It will surely be a wonderful weekend.",
                requiredStructures = listOf("let's", "why don't we", "how about", "we could")
            )
        )

        // Lesson 4: At the Airport
        registerTask(
            WritingTaskSpecification(
                lessonId = 4,
                lessonNumber = 4,
                activityId = "L4_AIRPORT_TIME_SEQUENCING",
                lessonTitle = "At the Airport",
                promptText = "Write 5–7 sentences about your experience at the airport using time sequencing words: First, Then, Next, After that, Finally.",
                taskType = "TIME_SEQUENCING_WRITING",
                targetGrammar = "Time Sequencing Connectors (First, Then, Next, After that, Finally)",
                targetVocabulary = "Airport, terminal, luggage, check-in, boarding pass, security, gate, flight",
                expectedResponseFormat = "A chronologically ordered narrative with 5–7 sentences using sequence connectors.",
                requiredItemCount = 5,
                minWords = 35,
                maxWords = 150,
                referenceSample = "First, I arrived at the international airport terminal two hours before my flight. Then, I proceeded to the airline check-in counter to drop off my heavy luggage and collect my boarding pass. Next, I walked through the security screening checkpoint with my carry-on bag. After that, I waited near departure gate 14 and enjoyed a cup of coffee. Finally, the flight attendants announced boarding and we boarded the aircraft smoothly.",
                requiredStructures = listOf("first", "then", "next", "after that", "finally")
            )
        )

        // Lesson 5: At the Hotel
        registerTask(
            WritingTaskSpecification(
                lessonId = 5,
                lessonNumber = 5,
                activityId = "L5_HOTEL_LINKING_WORDS",
                lessonTitle = "At the Hotel",
                promptText = "Write a short story using linking words (Because, Although, However) about booking and staying at a hotel.",
                taskType = "STORY_WRITING",
                targetGrammar = "Linking Words (Because, Although, However)",
                targetVocabulary = "Hotel, reservation, room, receptionist, view, stay, amenities, key card",
                expectedResponseFormat = "A short narrative about a hotel stay using the target linking connectors.",
                requiredItemCount = 4,
                minWords = 30,
                maxWords = 150,
                referenceSample = "Last summer, I booked a seaside hotel room because my family needed a relaxing vacation. Although the resort was crowded during the peak season, the front desk receptionist checked us in with great efficiency. However, the air conditioner in our bedroom was slightly noisy on the first night. The hotel manager promptly resolved the problem because he cared deeply about customer comfort.",
                requiredStructures = listOf("because", "although", "however")
            )
        )

        // Lesson 6: At the Police Station (Report a Theft)
        registerTask(
            WritingTaskSpecification(
                lessonId = 6,
                lessonNumber = 6,
                activityId = "L6_POLICE_REPORT",
                lessonTitle = "At the Police Station (Report a Theft)",
                promptText = "Write a short police report (6–12 sentences) describing a lost or stolen item and what the suspect looked like.",
                taskType = "POLICE_REPORT",
                targetGrammar = "Formal Past Tense & Suspect Description",
                targetVocabulary = "Police, officer, theft, stolen, victim, suspect, witness, description, jacket",
                expectedResponseFormat = "Formal police incident report detailing the event, missing property, and suspect description.",
                requiredItemCount = 6,
                minWords = 40,
                maxWords = 180,
                referenceSample = "Official Theft Incident Report. On October 12 at 4:30 PM, the victim reported the theft of a black leather backpack near Central Station. The backpack contained a silver laptop, university textbooks, and a personal wallet. According to the victim and an eyewitness, the suspect was a tall male approximately thirty years old wearing a dark hooded jacket and blue jeans. The suspect fled on foot toward Elm Street immediately after grabbing the bag.",
                requiredKeywords = listOf("stolen", "suspect", "description", "report")
            )
        )

        // Lesson 7: Health Problems
        registerTask(
            WritingTaskSpecification(
                lessonId = 7,
                lessonNumber = 7,
                activityId = "L7_HEALTH_DIARY_ENTRY",
                lessonTitle = "Health Problems",
                promptText = "Write a diary entry (120–150 words) about a time when you or someone you know was unwell, including symptoms and advice received.",
                taskType = "DIARY_ENTRY",
                targetGrammar = "Medical Expressions & Health Advice",
                targetVocabulary = "Fever, sore throat, cough, headache, doctor, prescription, medicine, rest, fluids",
                expectedResponseFormat = "A personal diary entry recounting illness symptoms and doctor's advice.",
                requiredItemCount = 5,
                minWords = 60,
                maxWords = 220,
                referenceSample = "Dear Diary, Today was an exhausting day because I came down with a severe flu. Yesterday morning, I woke up with a sharp headache and a painful sore throat. By afternoon, my body temperature had spiked, and I was shivering with a high fever and continuous coughing. My mother called the family clinic, and Dr. Adams advised me to stay in bed for three days, drink plenty of warm fluids, and take the prescribed paracetamol every eight hours. I followed the advice carefully and already feel slightly better tonight.",
                requiredKeywords = listOf("fever", "headache", "doctor", "advice", "rest")
            )
        )

        // Lesson 8: At the Restaurant
        registerTask(
            WritingTaskSpecification(
                lessonId = 8,
                lessonNumber = 8,
                activityId = "L8_RESTAURANT_REVIEW",
                lessonTitle = "At the Restaurant",
                promptText = "Write 10–15 sentences about your last dining experience at a restaurant (food, atmosphere, service, and paying the bill).",
                taskType = "RESTAURANT_REVIEW",
                targetGrammar = "Descriptive Sensory Language & Dining Expressions",
                targetVocabulary = "Restaurant, appetizer, main course, dessert, waiter, atmosphere, bill, delicious, tip",
                expectedResponseFormat = "A structured dining review addressing food, atmosphere, service, and paying the bill.",
                requiredItemCount = 8,
                minWords = 50,
                maxWords = 220,
                referenceSample = "Last Friday evening, my family and I had dinner at The Olive Tree restaurant downtown. The atmosphere was cozy and warm with soft instrumental music playing in the background. As soon as we sat down, a polite waiter brought us iced water and the evening menus. For appetizers, we ordered hot garlic bread and a fresh Caesar salad. For the main course, I ordered grilled salmon with steamed vegetables, which tasted incredibly delicious. The service was prompt and professional throughout the meal. Finally, we requested the bill and paid by credit card, leaving a generous tip for the waiter.",
                requiredKeywords = listOf("food", "atmosphere", "service", "bill")
            )
        )

        // Lesson 9: Inconveniences and Problems
        registerTask(
            WritingTaskSpecification(
                lessonId = 9,
                lessonNumber = 9,
                activityId = "L9_PROBLEM_SOLUTION",
                lessonTitle = "Inconveniences and Problems",
                promptText = "Write a short paragraph (60 words) about an inconvenience or problem you experienced recently and how it was solved.",
                taskType = "PROBLEM_SOLUTION_WRITING",
                targetGrammar = "Problem-Solution Discourse Phrases",
                targetVocabulary = "Inconvenience, problem, complain, customer service, refund, replacement, resolve",
                expectedResponseFormat = "A problem-solution paragraph explaining the issue and the resolution.",
                requiredItemCount = 4,
                minWords = 35,
                maxWords = 140,
                referenceSample = "Last Tuesday, I experienced a frustrating inconvenience when the wireless headphones I ordered online arrived with a broken charging port. I immediately called the company customer service hotline to explain the defect. The representative was extremely polite and apologized for the inconvenience. She promptly issued a free prepaid return label and dispatched a brand new replacement unit, which arrived in perfect condition two days later.",
                requiredKeywords = listOf("problem", "inconvenience", "solved")
            )
        )

        // Lesson 10: A Business Phone Call
        registerTask(
            WritingTaskSpecification(
                lessonId = 10,
                lessonNumber = 10,
                activityId = "L10_PHONE_DIALOGUE",
                lessonTitle = "A Business Phone Call",
                promptText = "Write a short professional phone conversation (5–10 lines) between an employee and a caller including greeting, introducing, taking a message, and polite closing.",
                taskType = "DIALOGUE_WRITING",
                targetGrammar = "Professional Telephone Register",
                targetVocabulary = "Speaking, may I speak to, take a message, call back, thanks for calling, goodbye",
                expectedResponseFormat = "Dialogue format with speaker turns (e.g. Employee: ... / Caller: ...)",
                requiredItemCount = 5,
                minWords = 30,
                maxWords = 160,
                referenceSample = "Employee: Good morning, Apex Solutions, Mark speaking. How may I help you today?\nCaller: Good morning. This is Sarah Jenkins from Horizon Tech. May I speak with Mr. Davis, please?\nEmployee: I'm afraid Mr. Davis is currently in a meeting. Would you like to leave a message?\nCaller: Yes, please. Could you ask him to call me back regarding the revised contract?\nEmployee: Certainly, Ms. Jenkins. I'll make sure he receives your message as soon as his meeting concludes. Thank you for calling.",
                requiredKeywords = listOf("speaking", "message", "calling")
            )
        )

        // Lesson 11: Handling Rude Customers
        registerTask(
            WritingTaskSpecification(
                lessonId = 11,
                lessonNumber = 11,
                activityId = "L11_ADVANCED_CONNECTORS",
                lessonTitle = "Handling Rude Customers",
                promptText = "Write a short text about dealing with a challenging customer service situation using linking words: However, Therefore, Furthermore, Nevertheless.",
                taskType = "ADVANCED_CONNECTORS_WRITING",
                targetGrammar = "Advanced Formal Connectors (However, Therefore, Furthermore, Nevertheless)",
                targetVocabulary = "Angry customer, empathy, calm, solution, professionalism, satisfaction",
                expectedResponseFormat = "A customer service explanation using the four advanced connectors.",
                requiredItemCount = 4,
                minWords = 35,
                maxWords = 180,
                referenceSample = "Dealing with angry customers requires high patience and active empathy. An upset client called yesterday demanding an immediate cancellation because his delivery was delayed. However, our agent maintained a calm and courteous tone without interrupting. Therefore, the customer gradually lowered his voice and explained the root issue. Furthermore, the agent offered free expedited shipping and a twenty percent discount on his next purchase. Nevertheless, resolving such escalations successfully depends on professional training and genuine listening.",
                requiredStructures = listOf("however", "therefore", "furthermore", "nevertheless")
            )
        )
    }

    fun registerTask(spec: WritingTaskSpecification) {
        tasks[spec.lessonNumber] = spec
    }

    fun resetToDefaults() {
        tasks.clear()
        registerBuiltInTasks()
    }

    fun getSpecificationForLesson(lessonNumber: Int): WritingTaskSpecification {
        return tasks[lessonNumber] ?: tasks[1]!!
    }

    fun getAllTasks(): List<WritingTaskSpecification> {
        return tasks.values.sortedBy { it.lessonNumber }
    }

    /**
     * Checks if a submitted text clearly belongs to a different lesson's task.
     * E.g., submitting Lesson 1's polite request instructions into Lesson 2's advice writing task.
     */
    fun detectTaskMismatch(studentText: String, currentLessonNumber: Int): TaskMismatchInfo? {
        val sLower = studentText.lowercase().trim()
        if (sLower.isBlank()) return null

        // Check if student pasted Lesson 1 polite requests into any other lesson
        if (currentLessonNumber != 1) {
            val hasL1Markers = sLower.contains("ask your teacher") ||
                    sLower.contains("ask your friend") ||
                    sLower.contains("ask someone to open") ||
                    sLower.contains("ask to borrow a phone") ||
                    (sLower.contains("open the window") && sLower.contains("borrow a phone"))

            if (hasL1Markers) {
                return TaskMismatchInfo(
                    mismatchedLessonNumber = 1,
                    mismatchedLessonTitle = "Making Requests",
                    explanationEn = "You submitted polite requests from Lesson 1 instead of fulfilling the current lesson's assignment.",
                    explanationAr = "لقد قمت بتقديم نصوص أو طلبات مهذبة تخص الدرس الأول (Making Requests) بدلاً من كتابة موضوع الدرس الحالي."
                )
            }
        }

        // Check if student pasted Lesson 2 advice into Lesson 1
        if (currentLessonNumber == 1) {
            if ((sLower.contains("advice for a student") || sLower.contains("improve english")) &&
                (sLower.contains("should") || sLower.contains("ought to")) &&
                !sLower.contains("could you") && !sLower.contains("would you")
            ) {
                return TaskMismatchInfo(
                    mismatchedLessonNumber = 2,
                    mismatchedLessonTitle = "Giving Advice",
                    explanationEn = "You submitted advice sentences from Lesson 2 instead of writing the four polite requests required for Lesson 1.",
                    explanationAr = "لقد قمت بتقديم نصائح تخص الدرس الثاني (Giving Advice) بدلاً من كتابة الطلبات المهذبة الأربعة المطلوبة في الدرس الأول."
                )
            }
        }

        // Check if student pasted Lesson 4 airport sequencing into another lesson
        if (currentLessonNumber != 4 && sLower.contains("airport") && sLower.contains("boarding pass") && sLower.contains("terminal")) {
            return TaskMismatchInfo(
                mismatchedLessonNumber = 4,
                mismatchedLessonTitle = "At the Airport",
                explanationEn = "You submitted airport narrative from Lesson 4 instead of the assigned topic.",
                explanationAr = "لقد قمت بتقديم موضوع عن المطار يخص الدرس الرابع بدلاً من موضوع الدرس الحالي."
            )
        }

        return null
    }
}

data class TaskMismatchInfo(
    val mismatchedLessonNumber: Int,
    val mismatchedLessonTitle: String,
    val explanationEn: String,
    val explanationAr: String
)
