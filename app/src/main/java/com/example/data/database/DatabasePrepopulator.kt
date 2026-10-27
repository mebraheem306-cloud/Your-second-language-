package com.example.data.database

import com.example.data.dao.EducationDao
import com.example.data.model.*
import org.json.JSONArray

object DatabasePrepopulator {

    suspend fun populateDatabase(dao: EducationDao) {
        if (dao.getLessonCount() > 0) return

        val lessons = listOf(
            LessonEntity(
                id = 1,
                lessonNumber = 1,
                title = "Making Requests",
                titleAr = "تقديم الطلبات بأدب",
                description = "Polite requests using Could you, Would you mind, Can you, May I. Library dialogue and Present Perfect for experiences.",
                sourceFilename = "Lesson_1_Making_Request.pdf",
                pageCount = 5,
                contentStatus = "VERIFIED_PDF",
                estimatedMinutes = 15
            ),
            LessonEntity(
                id = 2,
                lessonNumber = 2,
                title = "Giving Advice",
                titleAr = "تقديم النصيحة",
                description = "Giving advice using should, shouldn't, ought to. Health reading, dialogues, and Past Perfect Tense.",
                sourceFilename = "Lesson_2_Giving_Advice.pdf",
                pageCount = 7,
                contentStatus = "VERIFIED_PDF",
                estimatedMinutes = 20
            ),
            LessonEntity(
                id = 3,
                lessonNumber = 3,
                title = "Making Suggestions",
                titleAr = "تقديم الاقتراحات",
                description = "Suggestions using Let's, Why don't we, How about (+ing), We could. Accepting/rejecting suggestions and Past Continuous Tense.",
                sourceFilename = "Lesson_3_Making_Suggestions.pdf",
                pageCount = 7,
                contentStatus = "VERIFIED_PDF",
                estimatedMinutes = 20
            ),
            LessonEntity(
                id = 4,
                lessonNumber = 4,
                title = "At the Airport",
                titleAr = "في المطار",
                description = "Airport vocabulary, check-in dialogue, customs, boarding procedures, and Time Sequencing connectors.",
                sourceFilename = "Lesson_4_At_airport.pdf",
                pageCount = 7,
                contentStatus = "VERIFIED_PDF",
                estimatedMinutes = 20
            ),
            LessonEntity(
                id = 5,
                lessonNumber = 5,
                title = "At the Hotel",
                titleAr = "في الفندق",
                description = "Checking in and out, hotel amenities, dialogue practice, and Linking Words (Because, Although, However).",
                sourceFilename = "Lesson_5_At_the_Hotel.pdf",
                pageCount = 5,
                contentStatus = "VERIFIED_PDF",
                estimatedMinutes = 15
            ),
            LessonEntity(
                id = 6,
                lessonNumber = 6,
                title = "At the Police Station (Report a Theft)",
                titleAr = "في قسم الشرطة (الإبلاغ عن سرقة)",
                description = "Reporting a crime, suspect descriptions, emergency calls, formal vocabulary, and police report writing.",
                sourceFilename = "Lesson_6_At_the_police_Station.pdf",
                pageCount = 6,
                contentStatus = "VERIFIED_PDF",
                estimatedMinutes = 20
            ),
            LessonEntity(
                id = 7,
                lessonNumber = 7,
                title = "Health Problems",
                titleAr = "المشاكل الصحية والأمراض",
                description = "Flu vs Cold symptoms, medical advice, doctor's office consultation, compare and contrast analysis.",
                sourceFilename = "Lesson_7_Health_Problems.pdf",
                pageCount = 8,
                contentStatus = "VERIFIED_PDF",
                estimatedMinutes = 25
            ),
            LessonEntity(
                id = 8,
                lessonNumber = 8,
                title = "At the Restaurant",
                titleAr = "في المطعم",
                description = "Ordering food and drinks, dining reviews (The Golden Fork), waiter conversations, and paying the bill.",
                sourceFilename = "Lesson_8_At_the_restaurant.pdf",
                pageCount = 7,
                contentStatus = "VERIFIED_PDF",
                estimatedMinutes = 20
            ),
            LessonEntity(
                id = 9,
                lessonNumber = 9,
                title = "Inconveniences and Problems",
                titleAr = "المضايقات وحل المشاكل",
                description = "Complaining politely, call center customer service dialogues, giving solutions, and problem-solution writing.",
                sourceFilename = "Lesson_9_Inconveniences_and_Problems.pdf",
                pageCount = 5,
                contentStatus = "VERIFIED_PDF",
                estimatedMinutes = 18
            ),
            LessonEntity(
                id = 10,
                lessonNumber = 10,
                title = "A Business Phone Call",
                titleAr = "مكالمة عمل هاتفية",
                description = "Professional phone etiquette, taking messages, transferring calls, polite vs rude expressions in the workplace.",
                sourceFilename = "Lesson_10_A_Business_Phone_Call.pdf",
                pageCount = 6,
                contentStatus = "VERIFIED_PDF",
                estimatedMinutes = 20
            ),
            LessonEntity(
                id = 11,
                lessonNumber = 11,
                title = "Handling Rude Customers",
                titleAr = "التعامل مع العملاء الغاضبين",
                description = "De-escalating difficult customer situations, showing empathy, delivery delay call center case, and linking words (Therefore, Furthermore, Nevertheless).",
                sourceFilename = "Lesson_11_Handling_Rude_Customers.pdf",
                pageCount = 7,
                contentStatus = "VERIFIED_PDF",
                estimatedMinutes = 20
            )
        )
        dao.insertLessons(lessons)

        populateLessonContents(dao)
        populateGrammar(dao)
        populateVocabulary(dao)
        populateQuestions(dao)
        populateMatchingExercises(dao)
    }

    private suspend fun populateLessonContents(dao: EducationDao) {
        val contents = listOf(
            // Lesson 1
            LessonContentEntity(
                lessonId = 1,
                contentType = "EXPRESSIONS",
                title = "Useful Expressions: Polite Requests",
                contentText = "Could you…? -> Could you help me with this?\nWould you…? -> Would you close the door?\nCan you…? -> Can you lend me your pen?\nWould you mind…? -> Would you mind opening the window?\nCould I…? -> Could I use your phone?",
                sequenceNumber = 1,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 1,
                contentType = "READING",
                title = "Reading Comprehension: At the Library",
                contentText = "One afternoon, a student goes to the university library to study for his history class. He cannot find the book he needs, so he decides to ask the librarian for help.\n\nStudent: Excuse me, could you help me find a history book?\nLibrarian: Of course. What kind of history book do you need?\nStudent: I'm looking for a book about ancient Egypt. Could you show me where I can find it?\nLibrarian: Certainly. The history section is on the second floor.\nStudent: Thank you. Also, could I borrow the book if I find it?\nLibrarian: Yes, you can. You just need your student card.\nStudent: Great! Would you mind telling me how long I can keep the book?\nLibrarian: You can borrow it for two weeks.\nStudent: Thank you very much for your help.\nLibrarian: You're welcome. Follow me, and I will show you the section.",
                sequenceNumber = 2,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 1,
                contentType = "DIALOGUE",
                title = "Practically Speaking: Borrowing & Directions",
                contentText = "Last Saturday, before I went to the library, I called my friend and asked to use his bike.\n\nKen: Hi, Kud. Can I borrow your bike?\nKud: Sure.\nKen: Excuse me. Could you please give me directions to the library?\nKud: Of course. Go straight and then turn right. The library is on the left.",
                sequenceNumber = 3,
                sourcePage = 2
            ),
            LessonContentEntity(
                lessonId = 1,
                contentType = "GRAMMAR",
                title = "Grammar: Present Perfect Tense (Experiences)",
                contentText = "The present perfect tense (for experiences) is used to talk about things that happened at some point in your life without saying exactly when.\n\nStructure: Subject + have/has + past participle\nExamples:\n- I have visited Paris.\n- She has tried sushi.\n- They have seen that movie.\n\nCommon words used: ever (in questions), never (for negative experiences), before, once / twice / many times.",
                sequenceNumber = 4,
                sourcePage = 3
            ),

            // Lesson 2
            LessonContentEntity(
                lessonId = 2,
                contentType = "EXPRESSIONS",
                title = "Useful Expressions: Giving Advice",
                contentText = "You should… -> You should get more sleep.\nYou shouldn't… -> You shouldn't smoke beside the petrol station.\nYou ought to… -> You ought to consult a specialist.",
                sequenceNumber = 1,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 2,
                contentType = "READING",
                title = "Reading Comprehension: How to Stay Healthy",
                contentText = "Staying healthy is important. You should eat fresh food and exercise regularly. Have you ever considered how much sugar is hidden in the drinks and snacks you consume every day? You shouldn't eat too much fast food. If you feel stressed, you should relax and get enough sleep. It is also a good idea to drink plenty of water. After a long day, what small habit could you change right now to improve your physical or mental well-being?",
                sequenceNumber = 2,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 2,
                contentType = "DIALOGUE",
                title = "Practically Speaking: What's the Matter?",
                contentText = "A: What's the matter? | B: I'm really cold. | A: You should take a hot bath. | B: That's a good idea.\nA: What's the matter? | B: I have a terrible headache. | A: You should lie down. | B: That's a good idea.\nA: What's the matter? | B: I'm tired. | A: You should have some coffee or tea.\nA: What's the matter? | B: I ate a spicy pepper. | A: You should have some water.\nA: What's the matter? | B: I failed my test. | A: You should study harder.",
                sequenceNumber = 3,
                sourcePage = 2
            ),
            LessonContentEntity(
                lessonId = 2,
                contentType = "GRAMMAR",
                title = "Grammar: Past Perfect Tense",
                contentText = "The past perfect tense is used to describe an action that was completed before another action in the past.\n\nStructure: Subject + had + past participle (V3)\nExamples:\n- I had finished my homework before dinner.\n- She had left when I arrived.\n- They had already seen the movie.\n\nTime expressions: before, after, by the time, already, just, never.\nNegative: Subject + had not (hadn't) + past participle.",
                sequenceNumber = 4,
                sourcePage = 4
            ),

            // Lesson 3
            LessonContentEntity(
                lessonId = 3,
                contentType = "EXPRESSIONS",
                title = "Structures for Making Suggestions",
                contentText = "1. Let's + base verb -> Let's go to the park.\n2. Why don't we + base verb -> Why don't we watch a movie?\n3. How about / What about + verb (-ing) -> How about going out for dinner?\n4. We could + base verb -> We could study together.\n\nAccepting suggestions:\n- That's a great idea!\n- Sounds good!\n- OK, let's do it.\n\nRejecting politely:\n- I'd love to, but I'm feeling a bit tired.\n- Sorry, I prefer studying at home.",
                sequenceNumber = 1,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 3,
                contentType = "READING",
                title = "Reading Comprehension: Bored at Home",
                contentText = "Ahmed and Ali are bored at home. Ahmed says, 'Let's play football.' Ali replies, 'That's a good idea, but it's hot outside. How about watching a movie instead?' Ahmed agrees and says, 'Great! We could watch a comedy.'",
                sequenceNumber = 2,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 3,
                contentType = "GRAMMAR",
                title = "Grammar: Past Continuous Tense",
                contentText = "The past continuous tense (also called past progressive) is used to describe actions that were happening at a specific time in the past.\n\nStructure: Subject + was / were + verb + -ing\nExamples:\n- I was studying at 8 PM.\n- She was watching TV when I called (Interrupted action).\n- She was cooking while he was cleaning (Simultaneous actions).\n\nNegative: Subject + was / were + not + verb + -ing\nQuestion: Was / Were + subject + verb + -ing?",
                sequenceNumber = 3,
                sourcePage = 5
            ),

            // Lesson 4
            LessonContentEntity(
                lessonId = 4,
                contentType = "VOCABULARY",
                title = "Useful Airport Vocabulary",
                contentText = "Check-in desk/counter, Passport, Boarding pass, Layover / Stopover, Security checkpoint, Departure / Arrival, Gate / Terminal, Flight attendant, Carry-on luggage, Customs, Checked baggage, Overhead compartment.\nExample: 'I need to show my passport at the check-in counter.'",
                sequenceNumber = 1,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 4,
                contentType = "READING",
                title = "Reading Comprehension: At Heathrow Airport",
                contentText = "Maria arrived at Heathrow Airport two hours before her flight to Tokyo. First, she went to the British Airways check-in desk. The agent scanned her passport and asked if she was checking any luggage. Maria handed over her large suitcase and kept her carry-on with her laptop and books. After receiving her boarding pass, she proceeded to security. At the checkpoint, she placed her bag on the conveyor belt and walked through the metal detector. The agent asked her to remove her belt, which had set off the alarm. After a quick second scan, she was cleared. Maria found her gate, B17, and waited. When the announcement came for passengers with small children to board first, she prepared her documents. Soon, she was on the plane, settling into seat 34A. The flight attendant offered her a choice of meals, and Maria enjoyed a peaceful flight to Tokyo.",
                sequenceNumber = 2,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 4,
                contentType = "DIALOGUE",
                title = "Practically Speaking: Luggage & Seat Selection",
                contentText = "The officer: Hello, may I see your passport, please?\nThe passenger: Here you go.\nThe officer: Thank you. And where are you flying with us today?\nThe passenger: I'm going to London. I'll be studying there for a year.\nThe officer: How fun! Are you checking any bags today?\nThe passenger: Yes, just one.\nThe officer: Perfect. If you could place it on the scale... Hmm, it appears your bag is just a bit too heavy. Perhaps you could take something out of your bag and put it in your carry-on?\nThe passenger: Yes, I'll take this big book out of the bag. No problem.\nThe officer: Great. And I see you've chosen a window seat. Is that still okay?\nThe passenger: I think so. I've never flown before, so I thought it would be nice to look out at the clouds.",
                sequenceNumber = 3,
                sourcePage = 2
            ),
            LessonContentEntity(
                lessonId = 4,
                contentType = "GRAMMAR",
                title = "Grammar: Time Sequencing Connectors",
                contentText = "Sequencing steps describe order clearly:\n• First – used to introduce the beginning step\n• Then – used for the next action\n• Next – shows what comes after that\n• After that – shows subsequent step\n• Finally – used for the last step\n\nExample (Making an omelette):\nFirst, you break the eggs.\nThen, you heat a frying pan.\nNext, you pour the beaten eggs.\nAfter that, you cook for two minutes.\nFinally, you put the omelette into a plate.",
                sequenceNumber = 4,
                sourcePage = 4
            ),

            // Lesson 5
            LessonContentEntity(
                lessonId = 5,
                contentType = "VOCABULARY",
                title = "Useful Hotel Vocabulary",
                contentText = "Reservation = booking\nReception / Front desk = where guests check in\nCheck-in / Check-out\nSingle room / Double room\nKey card\nRoom service\nHousekeeping\nVacancy = available room\nAmenities = extra services (Wi-Fi, pool, gym)",
                sequenceNumber = 1,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 5,
                contentType = "READING",
                title = "Reading Comprehension: Checking into a Hotel",
                contentText = "Ali arrives at a hotel in the evening. He goes to the reception desk and says, 'Good evening. I have a reservation under the name Ali Hassan.' The receptionist smiles and asks for his passport. After checking the details, she gives him a key card and says, 'Your room is on the third floor. Breakfast is from 7 to 10 a.m.' Ali thanks her and goes to his room. He is happy because the room is clean and comfortable.",
                sequenceNumber = 2,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 5,
                contentType = "GRAMMAR",
                title = "Grammar: Linking Words (Because, Although, However)",
                contentText = "1. Because (Reason / Cause): Explains why something happens.\nStructure: Subject + verb + because + reason\nExample: I stayed home because I was tired.\n\n2. Although (Contrast): Shows contrast (surprising or unexpected).\nStructure: Although + clause, main clause OR Main clause + although + clause\nExample: Although it was raining, we went out.\n\n3. However (Contrast / Opposite idea): Shows contrast between two sentences.\nStructure: Sentence. However, sentence OR Sentence; however, sentence.\nExample: I wanted to go out. However, I had too much homework.",
                sequenceNumber = 3,
                sourcePage = 4
            ),

            // Lesson 6
            LessonContentEntity(
                lessonId = 6,
                contentType = "READING",
                title = "Incident Report: Theft at Green Street Bus Stop",
                contentText = "Name: Sarah Chen\nLocation: Green Street Bus Stop, near the public library\nDescription: On the afternoon of the incident, Ms. Chen was waiting at the Green Street bus stop for the number 42 bus. She was seated on a bench and had placed her backpack on the ground beside her feet. After a few minutes, a young man approached her and politely asked for the time. As Ms. Chen turned her attention to her watch to respond, the man suddenly grabbed her backpack and ran away toward the nearby park.\nMs. Chen immediately reacted and chased the suspect for approximately 20 meters. However, due to the crowd, she lost sight of him. An elderly woman standing nearby witnessed the incident and saw the suspect's face clearly.",
                sequenceNumber = 1,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 6,
                contentType = "DIALOGUE",
                title = "Emergency Call: Reporting a Robbery",
                contentText = "Officer: Police Department, what's your emergency?\nCaller: Hi, I just witnessed a robbery at a jewelry store.\nOfficer: Okay, try to stay calm. Can you tell me your exact location right now?\nCaller: I'm on Main Street, right outside Gold Time Jewelry. The front window is smashed.\nOfficer: Can you explain what you saw happen, step by step?\nCaller: A man broke the glass, grabbed some jewelry from the display, and ran away.\nOfficer: Did you get a clear look? What was he wearing?\nCaller: Yes, he was wearing a black hoodie with the hood up, dark jeans, and white sneakers. He had a short beard, too.\nOfficer: In which direction did he run?\nCaller: He ran down the alley next to the bakery. He was moving fast.",
                sequenceNumber = 2,
                sourcePage = 2
            ),

            // Lesson 7
            LessonContentEntity(
                lessonId = 7,
                contentType = "READING",
                title = "Medical Article: The Flu vs. The Common Cold",
                contentText = "Many people confuse the flu (influenza) with the common cold because they share similar symptoms. However, they are caused by different viruses, and the flu is generally much more severe.\nA cold usually develops gradually. You might start with a sore throat, followed by a runny nose, sneezing, and a mild cough. Most people can continue with their daily activities, and recovery typically takes about a week.\nThe flu, on the other hand, hits suddenly and hard. Symptoms include a high fever (usually above 38°C), severe muscle aches, extreme fatigue, and a headache. The flu can lead to serious complications in high-risk groups.\nPrevention is key: handwashing, avoiding sick people, healthy lifestyle, and annual vaccinations. If sick, rest is essential. Returning to work too early slows recovery and puts others at risk.",
                sequenceNumber = 1,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 7,
                contentType = "DIALOGUE",
                title = "At the Doctor's Office: Consultation",
                contentText = "Patient: Hi doctor, I'm not feeling well.\nDoctor: Hello, what seems to be the problem?\nPatient: I have a stomach ache. It started this morning.\nDoctor: Can you describe the pain? Is it sharp, dull, or cramping?\nPatient: It's more like cramping, and it comes and goes.\nDoctor: Any other symptoms? Fever, nausea, or vomiting?\nPatient: Yes, I feel a little nauseous. I had some street food yesterday.\nDoctor: You might have mild food poisoning. I will prescribe medicine for nausea and stomach pain. Drink water, eat light food like rice or soup, and avoid greasy food. Recovery usually takes two to three days.",
                sequenceNumber = 2,
                sourcePage = 3
            ),

            // Lesson 8
            LessonContentEntity(
                lessonId = 8,
                contentType = "READING",
                title = "Restaurant Review: Great Food, Slow Service",
                contentText = "Sarah J. wrote on social media: 'I visited The Golden Fork last Friday night with my colleagues. The atmosphere was cozy and dimly lit, perfect for a business dinner. We started with the bruschetta, which was fresh and flavorful. However, we waited nearly 20 minutes just to get our drinks ordered.\nFor the main course, I ordered the vegetarian pasta. It was generous in portion size but slightly under seasoned. My colleague had the ribeye steak, which he claimed was the best he'd had in years. Although the food quality was high, the pacing of the meal was off. We finished our mains but waited another 15 minutes for the check. Despite the service delays, I would recommend it for the food quality alone. Just don't go if you're in a rush!'",
                sequenceNumber = 1,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 8,
                contentType = "DIALOGUE",
                title = "Restaurant Dialogue: Ordering and Paying",
                contentText = "Customer: Good evening. I'd like a table for one, please.\nWaiter: Good evening. Do you prefer sitting by the window or near the center?\nCustomer: By the window, please.\nWaiter: What do you recommend today?\nWaiter: Our best seller is the grilled salmon with vegetables.\nCustomer: I'll have the grilled salmon and a glass of water, please.\n...\nCustomer: Excuse me, could I get the bill, please? Do you take credit cards?\nWaiter: Yes, we accept both credit and debit cards.",
                sequenceNumber = 2,
                sourcePage = 2
            ),

            // Lesson 9
            LessonContentEntity(
                lessonId = 9,
                contentType = "READING",
                title = "Reading Comprehension: A Difficult Day",
                contentText = "Last Monday, Ahmed had a very stressful day. First, his bus arrived 30 minutes late, so he missed an important meeting at work. Then, when he finally arrived at the office, his computer stopped working. He tried to fix it, but nothing helped.\nDuring lunch, he ordered a chicken sandwich at a café, but the waiter brought the wrong meal. Ahmed complained politely, and the waiter apologized and replaced the order.\nIn the evening, Ahmed went shopping, but the store had run out of the shoes he wanted. Although many inconveniences happened that day, Ahmed stayed calm and solved each problem one by one.",
                sequenceNumber = 1,
                sourcePage = 2
            ),
            LessonContentEntity(
                lessonId = 9,
                contentType = "EXPRESSIONS",
                title = "Complaining Politely & Customer Service Responses",
                contentText = "Complaining Politely:\n- Excuse me, there seems to be a problem.\n- I'm afraid this isn't working properly.\n- I'd like to make a complaint.\n- Unfortunately, my order hasn't arrived.\n\nResponding to Problems:\n- I'm sorry for the inconvenience.\n- Let me see what I can do.\n- We'll solve the issue as soon as possible.\n- Thank you for your patience.\n\nGiving Solutions:\n- Why don't we try another option?\n- You should contact customer service.\n- Maybe we can fix it together.\n- I suggest asking for a refund.",
                sequenceNumber = 2,
                sourcePage = 1
            ),

            // Lesson 10
            LessonContentEntity(
                lessonId = 10,
                contentType = "READING",
                title = "Workplace Communication: Phone Calls in the Workplace",
                contentText = "In many workplaces, phone calls play a crucial role in daily communication. Employees are often the first point of contact, so it is important to answer calls politely and professionally. A good phone manner creates a positive impression of the company.\nWhen answering a call, employees should greet the caller warmly and introduce themselves clearly. Listening carefully is essential to understand the caller's needs. Employees should avoid interrupting and should respond in a clear and helpful way.\nIf the person is not available, offer to take a message with caller's name, contact details, and purpose of the call. Good phone skills build trust, improve customer satisfaction, and maintain a professional image.",
                sequenceNumber = 1,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 10,
                contentType = "DIALOGUE",
                title = "Business Phone Dialogue: Chic Boutique & Supplier",
                contentText = "Chic Boutique: Good morning, Chic Boutique. How may I help you?\nJohn Wright: I'd like to speak to Mr. Morgan, please.\nChic Boutique: Who's calling, please?\nJohn Wright: This is John Wright from International Fashion Supplies.\nChic Boutique: Sorry, I didn't catch your name.\nJohn Wright: John Wright. That's W-R-I-G-H-T.\nChic Boutique: Okay, Mr. Wright. I'll try and put you through. I'm afraid the line's engaged. Would you like to hold?\nJohn Wright: Uh, can I leave a message?\nChic Boutique: Certainly.\nJohn Wright: Could you please tell Mr. Morgan our latest shipment has been delayed and that the 300 dresses should arrive next Friday?\nChic Boutique: Dress order delayed. Arriving next Friday. Could you let me have your number, please?\nJohn Wright: Yes, it's 0-1-6-3-2-9-6-0-9-9-4.\nChic Boutique: Thanks for your help. Goodbye.",
                sequenceNumber = 2,
                sourcePage = 2
            ),

            // Lesson 11
            LessonContentEntity(
                lessonId = 11,
                contentType = "READING",
                title = "Customer Support: Dealing with Difficult Customers",
                contentText = "Working in customer service can sometimes be challenging. Not all customers are polite, and some may express their frustration in a rude way. However, it is important for employees to remain calm and professional at all times.\nWhen facing a rude customer, the first step is to listen carefully. Often, customers just want to feel heard. Avoid interrupting them and show empathy by saying phrases like, 'I understand how you feel.'\nNext, try to find a solution. If possible, offer options to solve the problem. If the situation becomes too difficult, you can politely ask a supervisor for help.\nFinally, always stay respectful, even if the customer is not. This helps maintain a positive image for the company and can sometimes calm the customer down.",
                sequenceNumber = 1,
                sourcePage = 1
            ),
            LessonContentEntity(
                lessonId = 11,
                contentType = "DIALOGUE",
                title = "Call Center Case: Product Delivery Delay",
                contentText = "Call Center Agent: Thank you for calling Single Step Customer Service. My name is Carol. How can I assist you today?\nCustomer: Finally! Took you long enough to answer. I ordered a package and it's not here yet. What is the problem with you people? I selected Expedited Delivery.\nAgent: I'm very sorry to hear about the delay with your package. I'll do my best to help. May I have your order number, please?\nCustomer: Ugh, fine. It's 583-6002.\nAgent: Thank you. I see your package is currently in transit, but there have been weather delays. It should arrive by the end of the week. I understand the urgency, and I do apologize for any inconvenience.\nCustomer: This is totally unacceptable. I needed it yesterday. Just fix it!",
                sequenceNumber = 2,
                sourcePage = 2
            ),
            LessonContentEntity(
                lessonId = 11,
                contentType = "GRAMMAR",
                title = "Grammar: Advanced Linking Words",
                contentText = "However, Therefore, Furthermore, Nevertheless:\n\n1. However (Contrast / تناقض): Shows contrast between two thoughts.\n2. Therefore (Result / نتيجة): Indicates that something happens as a direct consequence.\n3. Furthermore (Addition / بالإضافة لذلك): Adds more information to support the point.\n4. Nevertheless (Unexpected contrast / رغم ذلك): Shows an unexpected concession.\n\nExample text:\nEducation is very important in our lives. It helps people find better jobs; therefore, many students work hard to succeed. However, studying can sometimes be stressful and tiring. Furthermore, education improves our knowledge and skills. Nevertheless, students should stay motivated and continue learning despite difficulties.",
                sequenceNumber = 3,
                sourcePage = 5
            )
        )
        dao.insertLessonContents(contents)
    }

    private suspend fun populateGrammar(dao: EducationDao) {
        val grammarRules = listOf(
            GrammarRuleEntity(
                title = "Present Perfect Tense (Experiences)",
                titleAr = "المضارع التام (للتجارب والخبرات الحياتية)",
                explanationEn = "The present perfect tense for experiences is used to talk about things that happened at some point in your life without saying exactly when.",
                explanationAr = "يُستخدم زمن المضارع التام للتعبير عن أحداث أو تجارب وقعت في وقت غير محدد في حياة الشخص دون ذكر توقيت حدوثها بالضبط.",
                usage = "Used with words like 'ever', 'never', 'before', 'once/twice'.",
                usageAr = "يُستخدم بكثرة مع الكلمات الدالة: ever (في السؤال)، never (للنفي)، before (من قبل)، once/twice (مرة أو مرتين).",
                structure = "Subject + have/has + Past Participle (V3)",
                affirmativeForm = "I have visited Paris. / She has tried sushi.",
                negativeForm = "I have never tried skydiving. / He has not seen that movie.",
                questionForm = "Have you ever been to Egypt? / Has she finished the report?",
                commonMistakes = "Mistake: 'I have visited Paris yesterday.' -> Incorrect! If a specific past time is given, use Past Simple: 'I visited Paris yesterday.'",
                sourceLessonId = 1,
                sourcePage = 3
            ),
            GrammarRuleEntity(
                title = "Giving Advice (Should, Shouldn't, Ought to)",
                titleAr = "تقديم النصيحة (Should / Shouldn't / Ought to)",
                explanationEn = "We use 'should', 'shouldn't', and 'ought to' to give advice or express what is the right or recommended thing to do.",
                explanationAr = "نستخدم should و shouldn't و ought to لإعطاء نصيحة أو التعبير عن الرأي بما يُفضل فعله أو تجنبه.",
                usage = "Followed by a bare infinitive (base form of the verb).",
                usageAr = "يأتي الفعل بعدهم دائماً في المصدر مجرداً من أي إضافات.",
                structure = "Subject + should / ought to + base verb",
                affirmativeForm = "You should get more sleep. / You ought to exercise three times a week.",
                negativeForm = "You shouldn't eat too much fast food. / You shouldn't smoke beside the petrol station.",
                questionForm = "Should I drink more water? / What should we do now?",
                commonMistakes = "Mistake: 'You should to sleep.' -> Incorrect! 'should' takes base verb without 'to'. Only 'ought' requires 'to'.",
                sourceLessonId = 2,
                sourcePage = 1
            ),
            GrammarRuleEntity(
                title = "Past Perfect Tense",
                titleAr = "الماضي التام (الحدث الأقدم في الماضي)",
                explanationEn = "The past perfect tense is used to describe an action that was completed before another action in the past.",
                explanationAr = "يُستخدم الماضي التام لتوضيح الحدث الذي اكتمل أولاً قبل وقوع حدث آخر في الماضي.",
                usage = "Clarifies sequence of two past events: the earlier event uses Past Perfect, the later event uses Past Simple.",
                usageAr = "يرتب حدثين في الماضي: الحدث الأقدم يأخذ Past Perfect، والحدث اللاحق يأخذ Past Simple.",
                structure = "Subject + had + Past Participle (V3)",
                affirmativeForm = "I had finished my homework before dinner. / By the time we got there, the train had departed.",
                negativeForm = "I hadn't eaten before the meeting. / He had never visited that place before.",
                questionForm = "Had she left when you arrived?",
                commonMistakes = "Mistake: Using Past Perfect for both events. Only the first completed action takes 'had + V3'.",
                sourceLessonId = 2,
                sourcePage = 4
            ),
            GrammarRuleEntity(
                title = "Making Suggestions (Let's, Why don't we, How about)",
                titleAr = "صيغ تقديم الاقتراحات",
                explanationEn = "Different structures can propose activities politely to friends or colleagues.",
                explanationAr = "طرق متنوعة لتقديم اقتراحات للأنشطة المشتركة مع الأصدقاء أو زملاء العمل.",
                usage = "Pay close attention to verb form: Let's / Why don't we / We could take base verb; How about / What about take -ing.",
                usageAr = "انتبه لصيغة الفعل: Let's و Why don't we و We could يتبعها فعل مجرد. أما How about فيتبعها verb + ing.",
                structure = "Let's + base verb | Why don't we + base verb | How about + verb(-ing)",
                affirmativeForm = "Let's go to the park. / How about watching a movie? / We could study together.",
                negativeForm = "Why don't we not order pizza tonight? / Let's not stay up late.",
                questionForm = "Why don't we play cards? / How about going out for dinner?",
                commonMistakes = "Mistake: 'How about watch a movie?' -> Incorrect! Must be: 'How about watching a movie?'",
                sourceLessonId = 3,
                sourcePage = 1
            ),
            GrammarRuleEntity(
                title = "Past Continuous Tense",
                titleAr = "الماضي المستمر (الحدث المستمر في وقت محدد بالماضي)",
                explanationEn = "Used to describe actions that were in progress at a specific time in the past, or interrupted by another action.",
                explanationAr = "يُستخدم لوصف فعل كان مستمراً في لحظة معينة في الماضي، أو فعل كان مستمراً وقاطعه حدث آخر.",
                usage = "1. Action in progress at 8 PM. 2. Interrupted: 'was watching TV when I called'. 3. Parallel: 'cooking while he was cleaning'.",
                usageAr = "١. فعل مستمر في وقت محدد. ٢. فعل قاطعه حدث آخر (when + past simple). ٣. حدثان متزامنان (while).",
                structure = "Subject + was/were + verb(-ing)",
                affirmativeForm = "I was studying at 8 PM. / They were playing football when it started to rain.",
                negativeForm = "She was not studying at 9 PM. / They weren't working.",
                questionForm = "Were you sleeping when I called? / Was he playing?",
                commonMistakes = "Mistake: Forgetting 'was/were' or using base verb instead of -ing form.",
                sourceLessonId = 3,
                sourcePage = 5
            ),
            GrammarRuleEntity(
                title = "Time Sequencing Connectors",
                titleAr = "أدوات الترتيب الزمني والخطوات",
                explanationEn = "Connectors used to describe a clear chronological order of steps or narrative events.",
                explanationAr = "أدوات الربط الزمنية المستخدمة لسرد تسلسل منطقي للخطوات أو الأحداث اليومية.",
                usage = "First (beginning) -> Then (next action) -> Next -> After that -> Finally (conclusion).",
                usageAr = "First (البداية) -> Then (الخطوة التالية) -> Next -> After that -> Finally (الخطوة الأخيرة).",
                structure = "Connector + , + Subject + Verb",
                affirmativeForm = "First, you break the eggs. Then, you heat a frying pan. Finally, you put the omelette on a plate.",
                negativeForm = "First, do not turn on the power before checking cables.",
                questionForm = "What do you do next?",
                commonMistakes = "Mistake: Confusing the order of 'First' and 'Finally'.",
                sourceLessonId = 4,
                sourcePage = 4
            ),
            GrammarRuleEntity(
                title = "Linking Words: Because, Although, However",
                titleAr = "أدوات الربط: السبب والتباين (Because / Although / However)",
                explanationEn = "Words that connect ideas to show reason or contrast.",
                explanationAr = "أدوات ربط توضح إما السبب (Because) أو التناقض المفاجئ (Although / However).",
                usage = "Because = Reason. Although = Subordinating contrast (within sentence). However = Transition word between sentences.",
                usageAr = "Because = السبب. Although = التناقض (تربط جملتين معاً). However = تفصل بين جملتين مستقلتين وتُتبع بفاصلة.",
                structure = "Sentence + because + reason | Although + clause, main clause | Sentence. However, sentence.",
                affirmativeForm = "She is happy because she passed. / Although it was raining, we went out. / I wanted to go out. However, I had too much homework.",
                negativeForm = "He didn't come because he was sick.",
                questionForm = "Did you go although it was cold?",
                commonMistakes = "Mistake: Putting 'However' without proper punctuation. In writing, use a period or semicolon before it and a comma after it.",
                sourceLessonId = 5,
                sourcePage = 4
            ),
            GrammarRuleEntity(
                title = "Advanced Connectors: Therefore, Furthermore, Nevertheless",
                titleAr = "أدوات الربط المتقدمة: النتيجة والإضافة والاستدراك",
                explanationEn = "Formal connectors used in writing and professional communication.",
                explanationAr = "أدوات ربط متقدمة تستخدم في الكتابة المهنية والأكاديمية.",
                usage = "Therefore = Result (نتيجة). Furthermore = Addition (إضافة). Nevertheless = Unexpected contrast (رغم ذلك).",
                usageAr = "Therefore تفيد النتيجة، Furthermore تفيد الإضافة، و Nevertheless تفيد الاستدراك رغم الصعوبات.",
                structure = "Sentence; therefore, sentence. / Sentence. Furthermore, sentence. / Sentence. Nevertheless, sentence.",
                affirmativeForm = "Education helps people; therefore, students work hard. Furthermore, it improves skills. Nevertheless, they stay motivated.",
                negativeForm = "He did not study; therefore, he could not pass.",
                questionForm = "Why, therefore, did you make this decision?",
                commonMistakes = "Mistake: Using 'Therefore' when showing contrast instead of cause-effect result.",
                sourceLessonId = 11,
                sourcePage = 5
            )
        )
        val ruleIds = dao.insertGrammarRules(grammarRules)

        // Examples for rule 1 (Present Perfect)
        val examples = listOf(
            GrammarExampleEntity(
                grammarRuleId = ruleIds[0],
                exampleText = "I have visited Paris.",
                exampleTranslationAr = "لقد زرت باريس (في وقت ما من حياتي).",
                isOriginalSourceExample = true
            ),
            GrammarExampleEntity(
                grammarRuleId = ruleIds[0],
                exampleText = "She has tried sushi.",
                exampleTranslationAr = "لقد جربت السوشي.",
                isOriginalSourceExample = true
            ),
            GrammarExampleEntity(
                grammarRuleId = ruleIds[0],
                exampleText = "They have seen that movie.",
                exampleTranslationAr = "لقد شاهدوا هذا الفيلم.",
                isOriginalSourceExample = true
            ),
            GrammarExampleEntity(
                grammarRuleId = ruleIds[0],
                exampleText = "Have you ever been to Egypt?",
                exampleTranslationAr = "هل سبق لك أن زرت مصر من قبل؟",
                isOriginalSourceExample = true
            ),
            GrammarExampleEntity(
                grammarRuleId = ruleIds[0],
                exampleText = "I have never tried skydiving.",
                exampleTranslationAr = "لم أجرب القفز بالمظلات قط.",
                isOriginalSourceExample = true
            ),
            // Examples for rule 2 (Advice)
            GrammarExampleEntity(
                grammarRuleId = ruleIds[1],
                exampleText = "You should get more sleep.",
                exampleTranslationAr = "ينبغي عليك الحصول على مزيد من النوم.",
                isOriginalSourceExample = true
            ),
            GrammarExampleEntity(
                grammarRuleId = ruleIds[1],
                exampleText = "You shouldn't smoke beside the petrol station.",
                exampleTranslationAr = "لا ينبغي لك التدخين بجوار محطة الوقود.",
                isOriginalSourceExample = true
            ),
            GrammarExampleEntity(
                grammarRuleId = ruleIds[1],
                exampleText = "You ought to exercise three times a week.",
                exampleTranslationAr = "يجب عليك ممارسة الرياضة ثلاث مرات أسبوعياً.",
                isOriginalSourceExample = true
            ),
            // Examples for rule 3 (Past Perfect)
            GrammarExampleEntity(
                grammarRuleId = ruleIds[2],
                exampleText = "I had finished my homework before dinner.",
                exampleTranslationAr = "كنت قد أنهيت واجبي قبل العشاء.",
                isOriginalSourceExample = true
            ),
            GrammarExampleEntity(
                grammarRuleId = ruleIds[2],
                exampleText = "By the time we got there, the train had departed.",
                exampleTranslationAr = "بحلول وقت وصولنا إلى هناك، كان القطار قد غادر بالفعل.",
                isOriginalSourceExample = true
            )
        )
        dao.insertGrammarExamples(examples)
    }

    private suspend fun populateVocabulary(dao: EducationDao) {
        val vocab = listOf(
            // Lesson 4 Airport
            VocabularyEntity(
                word = "Boarding pass",
                meaningAr = "بطاقة صعود الطائرة",
                partOfSpeech = "Noun",
                definitionEn = "A document that lets you get on the plane",
                exampleSentence = "You must show your boarding pass at the gate.",
                sourceLessonId = 4,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Carry-on luggage",
                meaningAr = "حقيبة اليد (المحمولة داخل الكابينة)",
                partOfSpeech = "Noun",
                definitionEn = "Bags you take onto the plane with you",
                exampleSentence = "Maria kept her laptop and books in her carry-on.",
                sourceLessonId = 4,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Customs",
                meaningAr = "الجمارك",
                partOfSpeech = "Noun",
                definitionEn = "Area where officers check items you bring from another country",
                exampleSentence = "Pass through customs after claiming your baggage.",
                sourceLessonId = 4,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Layover",
                meaningAr = "توقف ترانزيت بين الرحلات",
                partOfSpeech = "Noun",
                definitionEn = "A short stop between flights",
                exampleSentence = "We have a two-hour layover in Frankfurt.",
                sourceLessonId = 4,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Overhead compartment",
                meaningAr = "خزانة الأمتعة العلوية في الطائرة",
                partOfSpeech = "Noun",
                definitionEn = "The storage space above your seat on the plane",
                exampleSentence = "Please place your smaller bag in the overhead compartment.",
                sourceLessonId = 4,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Flight attendant",
                meaningAr = "مضيف / مضيفة طيران",
                partOfSpeech = "Noun",
                definitionEn = "A person who helps passengers and serves meals on the plane",
                exampleSentence = "The flight attendant offered her a choice of meals.",
                sourceLessonId = 4,
                sourcePage = 1
            ),

            // Lesson 5 Hotel
            VocabularyEntity(
                word = "Reservation",
                meaningAr = "حجز مسبق",
                partOfSpeech = "Noun",
                definitionEn = "Booking an accommodation or service in advance",
                exampleSentence = "I have a reservation under the name Ali Hassan.",
                sourceLessonId = 5,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Front desk",
                meaningAr = "مكتب الاستقبال",
                partOfSpeech = "Noun",
                definitionEn = "The desk where guests check in at a hotel",
                exampleSentence = "When you first arrive at a hotel, you go to the front desk to check-in.",
                sourceLessonId = 5,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Key card",
                meaningAr = "بطاقة المفتاح الإلكترونية",
                partOfSpeech = "Noun",
                definitionEn = "An electronic plastic card used to unlock hotel room doors",
                exampleSentence = "Here is your key card. Your room is 208 on the second floor.",
                sourceLessonId = 5,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Amenities",
                meaningAr = "المرافق والخدمات الإضافية",
                partOfSpeech = "Noun",
                definitionEn = "Extra services and facilities (Wi-Fi, pool, gym)",
                exampleSentence = "The hotel amenities include a swimming pool and complimentary breakfast.",
                sourceLessonId = 5,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Room service",
                meaningAr = "خدمة الغرف",
                partOfSpeech = "Noun",
                definitionEn = "Food and beverage delivery service directly to a hotel room",
                exampleSentence = "If you're hungry late at night, you can order room service.",
                sourceLessonId = 5,
                sourcePage = 1
            ),

            // Lesson 6 Police
            VocabularyEntity(
                word = "Suspect",
                meaningAr = "مشتبه به",
                partOfSpeech = "Noun",
                definitionEn = "A person believed to be guilty of a crime",
                exampleSentence = "The police are looking for the suspect who wore a red jacket.",
                sourceLessonId = 6,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Witness",
                meaningAr = "شاهد عيان",
                partOfSpeech = "Noun",
                definitionEn = "A person who sees a crime or incident happen",
                exampleSentence = "Did anyone see what happened? We need a witness.",
                sourceLessonId = 6,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Evidence",
                meaningAr = "دليل / إثبات",
                partOfSpeech = "Noun",
                definitionEn = "Information or objects used to prove that a crime happened",
                exampleSentence = "The broken window was collected as forensic evidence.",
                sourceLessonId = 6,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Theft",
                meaningAr = "سرقة",
                partOfSpeech = "Noun",
                definitionEn = "The crime of taking something that does not belong to you without permission",
                exampleSentence = "I need to report a theft. My backpack was taken.",
                sourceLessonId = 6,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Arrest",
                meaningAr = "إلقاء القبض / احتجاز",
                partOfSpeech = "Verb / Noun",
                definitionEn = "When police take someone into custody because they believe this person committed a crime",
                exampleSentence = "The police arrested the suspect near the park.",
                sourceLessonId = 6,
                sourcePage = 1
            ),

            // Lesson 7 Health
            VocabularyEntity(
                word = "Fever",
                meaningAr = "حمى / ارتفاع درجة الحرارة",
                partOfSpeech = "Noun",
                definitionEn = "When your body temperature is higher than normal (above 38°C)",
                exampleSentence = "Symptoms of the flu include a high fever and muscle aches.",
                sourceLessonId = 7,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Nausea",
                meaningAr = "غثيان",
                partOfSpeech = "Noun",
                definitionEn = "A feeling of sickness with an inclination to vomit",
                exampleSentence = "The patient felt nausea after eating contaminated street food.",
                sourceLessonId = 7,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Fatigue",
                meaningAr = "إرهاق شديد / تعب",
                partOfSpeech = "Noun",
                definitionEn = "Extreme tiredness or lack of energy",
                exampleSentence = "Influenza causes severe muscle aches and extreme fatigue.",
                sourceLessonId = 7,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Prescribe",
                meaningAr = "يصف دواءً (روشتة)",
                partOfSpeech = "Verb",
                definitionEn = "Authorize the use of medicine for a patient by a doctor",
                exampleSentence = "The doctor will prescribe some medicine for stomach pain.",
                sourceLessonId = 7,
                sourcePage = 1
            ),

            // Lesson 8 Restaurant
            VocabularyEntity(
                word = "Dish",
                meaningAr = "طبق / وجبة معينة",
                partOfSpeech = "Noun",
                definitionEn = "A specific item of food prepared in a particular way",
                exampleSentence = "Our best-selling dish is grilled salmon with vegetables.",
                sourceLessonId = 8,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Atmosphere",
                meaningAr = "أجواء المكان",
                partOfSpeech = "Noun",
                definitionEn = "The mood or character of a place",
                exampleSentence = "The atmosphere in the restaurant was cozy and quiet, perfect for talking.",
                sourceLessonId = 8,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Receipt",
                meaningAr = "إيصال الدفع / فاتورة",
                partOfSpeech = "Noun",
                definitionEn = "A printed slip showing that money has been paid",
                exampleSentence = "After we finished eating, the waiter brought us the receipt so we could pay.",
                sourceLessonId = 8,
                sourcePage = 1
            ),

            // Lesson 9 Problems
            VocabularyEntity(
                word = "Inconvenience",
                meaningAr = "مضايقة / إزعاج خفيف",
                partOfSpeech = "Noun",
                definitionEn = "A small problem or difficulty",
                exampleSentence = "I apologize for the inconvenience caused by the delayed order.",
                sourceLessonId = 9,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Delay",
                meaningAr = "تأخير",
                partOfSpeech = "Noun / Verb",
                definitionEn = "When something happens later than planned",
                exampleSentence = "His bus arrived 30 minutes late due to traffic delay.",
                sourceLessonId = 9,
                sourcePage = 1
            ),
            VocabularyEntity(
                word = "Misunderstanding",
                meaningAr = "سوء تفاهم",
                partOfSpeech = "Noun",
                definitionEn = "A failure to understand correctly",
                exampleSentence = "The wrong dish was delivered due to a misunderstanding in the kitchen.",
                sourceLessonId = 9,
                sourcePage = 1
            ),

            // Lesson 10 & 11 Workplace Phone & Customer
            VocabularyEntity(
                word = "Line is engaged",
                meaningAr = "الخط مشغول",
                partOfSpeech = "Phrase",
                definitionEn = "The person is currently busy on another telephone call",
                exampleSentence = "I'm afraid the line's engaged. Would you like to hold?",
                sourceLessonId = 10,
                sourcePage = 3
            ),
            VocabularyEntity(
                word = "Put you through",
                meaningAr = "تحويل المكالمة إلى الشخص المطلوب",
                partOfSpeech = "Phrase",
                definitionEn = "Connect a phone caller to another extension or person",
                exampleSentence = "Hold on a moment, I'll put you through to Ms. Lee.",
                sourceLessonId = 10,
                sourcePage = 5
            ),
            VocabularyEntity(
                word = "Empathy",
                meaningAr = "تعاطف وتفهم لمشاعر الآخرين",
                partOfSpeech = "Noun",
                definitionEn = "The ability to understand and share the feelings of another",
                exampleSentence = "Show empathy to rude customers by saying: 'I understand how you feel.'",
                sourceLessonId = 11,
                sourcePage = 1
            )
        )
        dao.insertVocabulary(vocab)
    }

    private suspend fun populateQuestions(dao: EducationDao) {
        val questions = listOf(
            // Lesson 1 Questions
            QuestionEntity(
                lessonId = 1,
                questionType = "TRUE_FALSE",
                questionText = "The student in the library is looking for a science book.",
                questionTextAr = "الطالب في المكتبة يبحث عن كتاب علوم.",
                optionsJson = JSONArray(listOf("True", "False")).toString(),
                correctAnswer = "False",
                explanationEn = "He is looking for a history book about ancient Egypt.",
                explanationAr = "خطأ - الطالب يبحث عن كتاب تاريخ عن مصر القديمة.",
                grammarTopic = "Reading Comprehension",
                sourcePage = 4
            ),
            QuestionEntity(
                lessonId = 1,
                questionType = "TRUE_FALSE",
                questionText = "The history section is on the second floor.",
                questionTextAr = "قسم التاريخ يقع في الطابق الثاني.",
                optionsJson = JSONArray(listOf("True", "False")).toString(),
                correctAnswer = "True",
                explanationEn = "The librarian explicitly says: 'The history section is on the second floor.'",
                explanationAr = "صحيح - أخبرته أمينة المكتبة أن قسم التاريخ في الطابق الثاني.",
                grammarTopic = "Reading Comprehension",
                sourcePage = 4
            ),
            QuestionEntity(
                lessonId = 1,
                questionType = "MULTIPLE_CHOICE",
                questionText = "How long can the student borrow and keep the library book?",
                questionTextAr = "ما المدة التي يمكن للطالب استعارة الكتاب والاحتفاظ به خلالها؟",
                optionsJson = JSONArray(listOf("One week", "Two weeks", "One month", "Three days")).toString(),
                correctAnswer = "Two weeks",
                explanationEn = "Librarian: 'You can borrow it for two weeks.'",
                explanationAr = "أسبوعان وفقاً لنص الحوار في المكتبة.",
                grammarTopic = "Reading Comprehension",
                sourcePage = 2
            ),
            QuestionEntity(
                lessonId = 1,
                questionType = "POLITE_IDENTIFY",
                questionText = "Identify if the following request is Polite (P) or Not Polite (NP): 'Give me your pen.'",
                questionTextAr = "حدد هل الطلب مهذب (P) أم غير مهذب (NP): 'Give me your pen.'",
                optionsJson = JSONArray(listOf("P (Polite)", "NP (Not Polite)")).toString(),
                correctAnswer = "NP (Not Polite)",
                explanationEn = "Direct imperative without 'please' or modal verbs is not polite.",
                explanationAr = "أمر مباشر خالي من عبارات اللباقة والتهذيب.",
                grammarTopic = "Polite Requests",
                sourcePage = 5
            ),
            QuestionEntity(
                lessonId = 1,
                questionType = "POLITE_IDENTIFY",
                questionText = "Identify if the following request is Polite (P) or Not Polite (NP): 'Would you mind closing the window?'",
                questionTextAr = "حدد هل الطلب مهذب أم لا: 'Would you mind closing the window?'",
                optionsJson = JSONArray(listOf("P (Polite)", "NP (Not Polite)")).toString(),
                correctAnswer = "P (Polite)",
                explanationEn = "'Would you mind...' is a standard polite English request.",
                explanationAr = "صيغة طلب مهذبة جداً باللغة الإنجليزية.",
                grammarTopic = "Polite Requests",
                sourcePage = 5
            ),
            QuestionEntity(
                lessonId = 1,
                questionType = "FILL_BLANK",
                questionText = "Would you __________ opening the window? (please / could / mind / borrow / help)",
                questionTextAr = "أكمل الفراغ: Would you ______ opening the window?",
                optionsJson = JSONArray(listOf("please", "could", "mind", "borrow", "help")).toString(),
                correctAnswer = "mind",
                explanationEn = "'Would you mind' is followed by gerund (-ing).",
                explanationAr = "التعبير الصحيح هو 'Would you mind' ويتبعه verb+ing.",
                grammarTopic = "Polite Requests",
                sourcePage = 5
            ),

            // Lesson 2 Questions
            QuestionEntity(
                lessonId = 2,
                questionType = "MULTIPLE_CHOICE",
                questionText = "Your friend has a cold. What is the best advice to give?",
                questionTextAr = "صديقك مصاب بنزلة برد. ما هي أفضل نصيحة تقدمها له؟",
                optionsJson = JSONArray(listOf("You should go swimming.", "You should rest and drink warm water.", "You shouldn't see a doctor.")).toString(),
                correctAnswer = "You should rest and drink warm water.",
                explanationEn = "Rest and warm fluids are the medically sound advice from Lesson 2.",
                explanationAr = "الراحة وشرب الماء الدافئ هي النصيحة الصحيحة.",
                grammarTopic = "Giving Advice",
                sourcePage = 6
            ),
            QuestionEntity(
                lessonId = 2,
                questionType = "MULTIPLE_CHOICE",
                questionText = "Someone wants to learn English faster. You say:",
                questionTextAr = "شخص يريد تعلم الإنجليزية بشكل أسرع. تقول له:",
                optionsJson = JSONArray(listOf("You ought to practice every day.", "You shouldn't speak with friends.", "You should avoid English movies.")).toString(),
                correctAnswer = "You ought to practice every day.",
                explanationEn = "'You ought to practice every day' uses ought to for positive recommendation.",
                explanationAr = "يجب عليك الممارسة يومياً لتقوية لغتك.",
                grammarTopic = "Giving Advice",
                sourcePage = 6
            ),
            QuestionEntity(
                lessonId = 2,
                questionType = "MULTIPLE_CHOICE",
                questionText = "He failed the test because he __________ (not study). (Combine Past Perfect + reason)",
                questionTextAr = "اختر الصيغة الصحيحة للماضي التام:",
                optionsJson = JSONArray(listOf("hadn't studied", "didn't studied", "has not studied", "was not studying")).toString(),
                correctAnswer = "hadn't studied",
                explanationEn = "Past Perfect structure: had not + past participle (hadn't studied).",
                explanationAr = "صيغة الماضي التام المنفي: hadn't studied.",
                grammarTopic = "Past Perfect Tense",
                sourcePage = 7
            ),

            // Lesson 3 Questions
            QuestionEntity(
                lessonId = 3,
                questionType = "MULTIPLE_CHOICE",
                questionText = "At 6 PM yesterday, I __________ (cook) dinner.",
                questionTextAr = "في الساعة 6 مساء الأمس، كنت أطهو العشاء:",
                optionsJson = JSONArray(listOf("was cooking", "were cooking", "cooked", "am cooking")).toString(),
                correctAnswer = "was cooking",
                explanationEn = "Specific past point in time with singular subject 'I' requires 'was cooking'.",
                explanationAr = "حدث مستمر في وقت محدد بالماضي مع الفاعل I يتطلب was cooking.",
                grammarTopic = "Past Continuous Tense",
                sourcePage = 7
            ),
            QuestionEntity(
                lessonId = 3,
                questionType = "MULTIPLE_CHOICE",
                questionText = "They were playing football when it __________ to rain.",
                questionTextAr = "كانوا يلعبون كرة القدم عندما بدأت تمطر:",
                optionsJson = JSONArray(listOf("started", "was starting", "had started", "starts")).toString(),
                correctAnswer = "started",
                explanationEn = "Interrupted action: Past Continuous (were playing) + when + Past Simple (started).",
                explanationAr = "الحدث القاطع لحدث مستمر يأتي بزمن الماضي البسيط (started).",
                grammarTopic = "Past Continuous Tense",
                sourcePage = 7
            ),
            QuestionEntity(
                lessonId = 3,
                questionType = "MULTIPLE_CHOICE",
                questionText = "Choose the correct suggestion structure:",
                questionTextAr = "اختر صيغة الاقتراح الصحيحة لغوياً:",
                optionsJson = JSONArray(listOf("How about going to the beach?", "How about to go to the beach?", "Let's going to the beach.", "Why don't we going to the beach?")).toString(),
                correctAnswer = "How about going to the beach?",
                explanationEn = "How about requires verb + ing. Let's and Why don't we require base verb.",
                explanationAr = "How about يأتي بعدها فعل ينتهي بـ ing.",
                grammarTopic = "Making Suggestions",
                sourcePage = 1
            ),

            // Lesson 4 Questions
            QuestionEntity(
                lessonId = 4,
                questionType = "FILL_BLANK",
                questionText = "At the __________ checkpoint, you cannot bring liquids over 100ml.",
                questionTextAr = "في نقطة تفتيش ______ لا يمكنك إدخال سوائل أكثر من 100 مل:",
                optionsJson = JSONArray(listOf("passport", "gate", "security", "checked baggage", "flight attendant")).toString(),
                correctAnswer = "security",
                explanationEn = "Security checkpoint handles luggage screening and liquid restrictions.",
                explanationAr = "نقطة التفتيش الأمني (security checkpoint).",
                grammarTopic = "Airport Vocabulary",
                sourcePage = 6
            ),
            QuestionEntity(
                lessonId = 4,
                questionType = "ERROR_CORRECTION",
                questionText = "Find and fix the error: 'She keep her laptop in the checked baggage.'",
                questionTextAr = "صحح الخطأ في الجملة السابقة:",
                optionsJson = JSONArray(listOf("She keeps her laptop in the carry-on luggage.", "She keeped her laptop in baggage.", "She kept her laptop on seat.", "She keeps luggage in checked.")).toString(),
                correctAnswer = "She keeps her laptop in the carry-on luggage.",
                explanationEn = "Subject-verb agreement (keeps) and electronics are kept in carry-on, not checked baggage.",
                explanationAr = "تصحيح الفعل (keeps) والكمبيوتر المحمول يوضع في الحقيبة المحمولة carry-on.",
                grammarTopic = "Error Correction",
                sourcePage = 7
            ),

            // Lesson 5 Questions
            QuestionEntity(
                lessonId = 5,
                questionType = "MULTIPLE_CHOICE",
                questionText = "I chose this hotel __________ it has a swimming pool and free Wi-Fi.",
                questionTextAr = "اختر أداة الربط المناسبة: اخترت هذا الفندق ... به مسبح وإنترنت مجاني.",
                optionsJson = JSONArray(listOf("because", "although", "however", "therefore")).toString(),
                correctAnswer = "because",
                explanationEn = "'Because' introduces the reason for choosing the hotel.",
                explanationAr = "Because تشرح سبب اختيار الفندق.",
                grammarTopic = "Linking Words",
                sourcePage = 5
            ),
            QuestionEntity(
                lessonId = 5,
                questionType = "MULTIPLE_CHOICE",
                questionText = "The room was small, __________ it was very clean and comfortable.",
                questionTextAr = "كانت الغرفة صغيرة، ... كانت نظيفة جداً ومريحة.",
                optionsJson = JSONArray(listOf("although", "because", "so", "first")).toString(),
                correctAnswer = "although",
                explanationEn = "Shows unexpected contrast between small size and great comfort.",
                explanationAr = "Although توضح التباين بين صغر الغرفة ونظافتها وراحتها.",
                grammarTopic = "Linking Words",
                sourcePage = 5
            ),

            // Lesson 6 Questions
            QuestionEntity(
                lessonId = 6,
                questionType = "TRUE_FALSE",
                questionText = "Sarah Chen caught the suspect after running 20 meters.",
                questionTextAr = "أمسكت سارة تشن بالمشتبه به بعد أن ركضت 20 متراً.",
                optionsJson = JSONArray(listOf("True", "False")).toString(),
                correctAnswer = "False",
                explanationEn = "She lost sight of him as he disappeared into the crowd.",
                explanationAr = "خطأ - فقدت أثره بسبب الزحام ولم تمسك به.",
                grammarTopic = "Reading Comprehension",
                sourcePage = 5
            ),
            QuestionEntity(
                lessonId = 6,
                questionType = "TRUE_FALSE",
                questionText = "An elderly woman saw the suspect's face clearly.",
                questionTextAr = "رأت سيدة مسنة وجه المشتبه به بوضوح.",
                optionsJson = JSONArray(listOf("True", "False")).toString(),
                correctAnswer = "True",
                explanationEn = "An elderly woman standing nearby reported that she saw the suspect's face clearly.",
                explanationAr = "صحيح - شاهدت سيدة مسنة وجهه بوضوح واستطاعت وصفه.",
                grammarTopic = "Reading Comprehension",
                sourcePage = 5
            ),

            // Lesson 7 Questions
            QuestionEntity(
                lessonId = 7,
                questionType = "MULTIPLE_CHOICE",
                questionText = "How does a common cold usually develop compared to the flu?",
                questionTextAr = "كيف تتطور نزلة البرد العادية مقارنة بالإنفلونزا؟",
                optionsJson = JSONArray(listOf("A cold develops gradually, while the flu hits suddenly and severely.", "Both hit instantly.", "The flu is milder than cold.", "Neither has fever.")).toString(),
                correctAnswer = "A cold develops gradually, while the flu hits suddenly and severely.",
                explanationEn = "Directly from the medical text comparing cold and influenza.",
                explanationAr = "نزلة البرد تتطور تدريجياً، بينما تضرب الإنفلونزا فجأة وبشدة.",
                grammarTopic = "Reading Comprehension",
                sourcePage = 2
            ),

            // Lesson 8 Questions
            QuestionEntity(
                lessonId = 8,
                questionType = "TRUE_FALSE",
                questionText = "Sarah found the service at The Golden Fork to be fast and efficient throughout the meal.",
                questionTextAr = "وجدت سارة أن الخدمة في مطعم The Golden Fork كانت سريعة وفعالة طوال الوجبة.",
                optionsJson = JSONArray(listOf("True", "False")).toString(),
                correctAnswer = "False",
                explanationEn = "She complained they waited nearly 20 minutes for drinks and 15 minutes for the check.",
                explanationAr = "خطأ - كانت الخدمة بطيئة وانتظروا 20 دقيقة للمشروبات و 15 دقيقة للفاتورة.",
                grammarTopic = "Reading Comprehension",
                sourcePage = 2
            ),

            // Lesson 10 Questions
            QuestionEntity(
                lessonId = 10,
                questionType = "MULTIPLE_CHOICE",
                questionText = "Rewrite the rude phrase 'What do you want?' into a polite business greeting:",
                questionTextAr = "أعد صياغة العبارة الفظة 'ماذا تريد؟' لتصبح احترافية ومهذبة:",
                optionsJson = JSONArray(listOf("How may I help you?", "Who are you?", "Tell me now.", "Wait a minute.")).toString(),
                correctAnswer = "How may I help you?",
                explanationEn = "'How may I help you?' is the standard professional business telephone greeting.",
                explanationAr = "'How may I help you?' (كيف يمكنني مساعدتك؟) هي العبارة المهذبة الصحيحة.",
                grammarTopic = "Business Etiquette",
                sourcePage = 6
            ),

            // Lesson 11 Questions
            QuestionEntity(
                lessonId = 11,
                questionType = "MULTIPLE_CHOICE",
                questionText = "Education is very important; __________, many students work hard to succeed.",
                questionTextAr = "التعليم مهم جداً؛ ... يجتهد العديد من الطلاب للنجاح.",
                optionsJson = JSONArray(listOf("therefore", "however", "nevertheless", "furthermore")).toString(),
                correctAnswer = "therefore",
                explanationEn = "'Therefore' indicates a result of the preceding statement.",
                explanationAr = "Therefore تفيد النتيجة المنطقية.",
                grammarTopic = "Advanced Connectors",
                sourcePage = 5
            ),
            QuestionEntity(
                lessonId = 11,
                questionType = "MULTIPLE_CHOICE",
                questionText = "When facing a rude customer, what is the FIRST step according to Lesson 11?",
                questionTextAr = "عند مواجهة عميل غاضب، ما هي الخطوة الأولى وفقاً للدرس 11؟",
                optionsJson = JSONArray(listOf("Listen carefully and show empathy.", "Argue back immediately.", "Hang up the phone.", "Tell them they are wrong.")).toString(),
                correctAnswer = "Listen carefully and show empathy.",
                explanationEn = "The text states: 'the first step is to listen carefully. Often, customers just want to feel heard.'",
                explanationAr = "الاستماع باهتمام وإظهار التفهم والتعاطف أولاً.",
                grammarTopic = "Customer Service",
                sourcePage = 1
            )
        )
        dao.insertQuestions(questions)
    }

    private suspend fun populateMatchingExercises(dao: EducationDao) {
        // Exercise 1: Lesson 1
        val ex1 = MatchingExerciseEntity(
            lessonId = 1,
            title = "Lesson 1: Request Expressions and Meanings",
            category = "EXPRESSION_MEANING",
            sourcePage = 4
        )
        val ex1Id = dao.insertMatchingExercises(listOf(ex1))[0]
        dao.insertMatchingPairs(
            listOf(
                MatchingPairEntity(exerciseId = ex1Id, leftItem = "Could you help me?", rightItem = "Requesting help", sequenceNumber = 1),
                MatchingPairEntity(exerciseId = ex1Id, leftItem = "May I borrow…?", rightItem = "Asking permission to take something", sequenceNumber = 2),
                MatchingPairEntity(exerciseId = ex1Id, leftItem = "Would you mind…?", rightItem = "Asking politely if something is okay", sequenceNumber = 3),
                MatchingPairEntity(exerciseId = ex1Id, leftItem = "Can you please…?", rightItem = "Asking someone to do something politely", sequenceNumber = 4)
            )
        )

        // Exercise 2: Lesson 2
        val ex2 = MatchingExerciseEntity(
            lessonId = 2,
            title = "Lesson 2: Health Problems and Advice",
            category = "PROBLEM_ADVICE",
            sourcePage = 7
        )
        val ex2Id = dao.insertMatchingExercises(listOf(ex2))[0]
        dao.insertMatchingPairs(
            listOf(
                MatchingPairEntity(exerciseId = ex2Id, leftItem = "I have a stomachache.", rightItem = "You shouldn't eat spicy food.", sequenceNumber = 1),
                MatchingPairEntity(exerciseId = ex2Id, leftItem = "It's very cold outside.", rightItem = "You should wear a jacket.", sequenceNumber = 2),
                MatchingPairEntity(exerciseId = ex2Id, leftItem = "I lost my wallet.", rightItem = "You ought to tell the teacher.", sequenceNumber = 3),
                MatchingPairEntity(exerciseId = ex2Id, leftItem = "I don't understand the lesson.", rightItem = "You should look for it or ask for help.", sequenceNumber = 4),
                MatchingPairEntity(exerciseId = ex2Id, leftItem = "My phone battery is low.", rightItem = "You should charge it now.", sequenceNumber = 5)
            )
        )

        // Exercise 3: Lesson 3
        val ex3 = MatchingExerciseEntity(
            lessonId = 3,
            title = "Lesson 3: Suggestion Starters and Completions",
            category = "GRAMMAR_STARTER",
            sourcePage = 6
        )
        val ex3Id = dao.insertMatchingExercises(listOf(ex3))[0]
        dao.insertMatchingPairs(
            listOf(
                MatchingPairEntity(exerciseId = ex3Id, leftItem = "Let's", rightItem = "play video games.", sequenceNumber = 1),
                MatchingPairEntity(exerciseId = ex3Id, leftItem = "Why don't we", rightItem = "visit our grandparents?", sequenceNumber = 2),
                MatchingPairEntity(exerciseId = ex3Id, leftItem = "How about", rightItem = "going to the beach?", sequenceNumber = 3),
                MatchingPairEntity(exerciseId = ex3Id, leftItem = "We could", rightItem = "order pizza for dinner.", sequenceNumber = 4)
            )
        )

        // Exercise 4: Lesson 4
        val ex4 = MatchingExerciseEntity(
            lessonId = 4,
            title = "Lesson 4: Airport Words and Definitions",
            category = "VOCAB_DEFINITION",
            sourcePage = 6
        )
        val ex4Id = dao.insertMatchingExercises(listOf(ex4))[0]
        dao.insertMatchingPairs(
            listOf(
                MatchingPairEntity(exerciseId = ex4Id, leftItem = "Boarding pass", rightItem = "A document that lets you get on the plane", sequenceNumber = 1),
                MatchingPairEntity(exerciseId = ex4Id, leftItem = "Carry-on", rightItem = "Bags you take onto the plane with you", sequenceNumber = 2),
                MatchingPairEntity(exerciseId = ex4Id, leftItem = "Customs", rightItem = "Area where officers check items you bring from another country", sequenceNumber = 3),
                MatchingPairEntity(exerciseId = ex4Id, leftItem = "Layover", rightItem = "A short stop between flights", sequenceNumber = 4),
                MatchingPairEntity(exerciseId = ex4Id, leftItem = "Overhead compartment", rightItem = "The storage space above your seat on the plane", sequenceNumber = 5)
            )
        )

        // Exercise 5: Lesson 6
        val ex5 = MatchingExerciseEntity(
            lessonId = 6,
            title = "Lesson 6: Legal & Police Terms",
            category = "VOCAB_DEFINITION",
            sourcePage = 5
        )
        val ex5Id = dao.insertMatchingExercises(listOf(ex5))[0]
        dao.insertMatchingPairs(
            listOf(
                MatchingPairEntity(exerciseId = ex5Id, leftItem = "Suspect", rightItem = "A person believed to be guilty of a crime.", sequenceNumber = 1),
                MatchingPairEntity(exerciseId = ex5Id, leftItem = "Witness", rightItem = "A person who sees a crime happen.", sequenceNumber = 2),
                MatchingPairEntity(exerciseId = ex5Id, leftItem = "Evidence", rightItem = "Information or objects used to prove that a crime happened.", sequenceNumber = 3),
                MatchingPairEntity(exerciseId = ex5Id, leftItem = "Theft", rightItem = "To take something that does not belong to you without permission.", sequenceNumber = 4),
                MatchingPairEntity(exerciseId = ex5Id, leftItem = "Arrest", rightItem = "When police take someone into custody.", sequenceNumber = 5)
            )
        )

        // Exercise 6: Lesson 7
        val ex6 = MatchingExerciseEntity(
            lessonId = 7,
            title = "Lesson 7: Health Terms and Definitions",
            category = "VOCAB_DEFINITION",
            sourcePage = 7
        )
        val ex6Id = dao.insertMatchingExercises(listOf(ex6))[0]
        dao.insertMatchingPairs(
            listOf(
                MatchingPairEntity(exerciseId = ex6Id, leftItem = "Fever", rightItem = "When your body temperature is higher than normal (above 38°C).", sequenceNumber = 1),
                MatchingPairEntity(exerciseId = ex6Id, leftItem = "Nausea", rightItem = "A feeling of sickness with an inclination to vomit.", sequenceNumber = 2),
                MatchingPairEntity(exerciseId = ex6Id, leftItem = "Stomach ache", rightItem = "Pain or discomfort in the stomach area.", sequenceNumber = 3),
                MatchingPairEntity(exerciseId = ex6Id, leftItem = "Fatigue", rightItem = "Extreme tiredness or lack of energy.", sequenceNumber = 4),
                MatchingPairEntity(exerciseId = ex6Id, leftItem = "Complications", rightItem = "Secondary problems or illnesses that result from a primary disease.", sequenceNumber = 5)
            )
        )

        // Exercise 7: Lesson 8
        val ex7 = MatchingExerciseEntity(
            lessonId = 8,
            title = "Lesson 8: Restaurant Terms",
            category = "VOCAB_DEFINITION",
            sourcePage = 6
        )
        val ex7Id = dao.insertMatchingExercises(listOf(ex7))[0]
        dao.insertMatchingPairs(
            listOf(
                MatchingPairEntity(exerciseId = ex7Id, leftItem = "Menu", rightItem = "A list of all the food and drinks available in a restaurant.", sequenceNumber = 1),
                MatchingPairEntity(exerciseId = ex7Id, leftItem = "Reservation", rightItem = "An arrangement to have a table kept for you at a specific time.", sequenceNumber = 2),
                MatchingPairEntity(exerciseId = ex7Id, leftItem = "Waiter/Waitress", rightItem = "A person whose job is to serve customers in a restaurant.", sequenceNumber = 3),
                MatchingPairEntity(exerciseId = ex7Id, leftItem = "Bill", rightItem = "The total amount of money you must pay for your meal.", sequenceNumber = 4),
                MatchingPairEntity(exerciseId = ex7Id, leftItem = "Dish", rightItem = "A specific item of food prepared in a particular way.", sequenceNumber = 5)
            )
        )

        // Exercise 8: Lesson 10
        val ex8 = MatchingExerciseEntity(
            lessonId = 10,
            title = "Lesson 10: Phone Expressions & Meanings",
            category = "EXPRESSION_MEANING",
            sourcePage = 5
        )
        val ex8Id = dao.insertMatchingExercises(listOf(ex8))[0]
        dao.insertMatchingPairs(
            listOf(
                MatchingPairEntity(exerciseId = ex8Id, leftItem = "I'll put you through.", rightItem = "I will connect you to the person you want.", sequenceNumber = 1),
                MatchingPairEntity(exerciseId = ex8Id, leftItem = "Who's calling, please?", rightItem = "Asking for the caller's identity politely.", sequenceNumber = 2),
                MatchingPairEntity(exerciseId = ex8Id, leftItem = "I'm afraid he/she is not available.", rightItem = "The person cannot come to the phone right now.", sequenceNumber = 3),
                MatchingPairEntity(exerciseId = ex8Id, leftItem = "Could you hold for a moment?", rightItem = "Please wait for a short time.", sequenceNumber = 4),
                MatchingPairEntity(exerciseId = ex8Id, leftItem = "The line is engaged.", rightItem = "The person is busy on another call.", sequenceNumber = 5)
            )
        )

        // Exercise 9: Lesson 11
        val ex9 = MatchingExerciseEntity(
            lessonId = 11,
            title = "Lesson 11: Linking Words & Rhetorical Functions",
            category = "EXPRESSION_MEANING",
            sourcePage = 5
        )
        val ex9Id = dao.insertMatchingExercises(listOf(ex9))[0]
        dao.insertMatchingPairs(
            listOf(
                MatchingPairEntity(exerciseId = ex9Id, leftItem = "However", rightItem = "contrast", sequenceNumber = 1),
                MatchingPairEntity(exerciseId = ex9Id, leftItem = "Therefore", rightItem = "result", sequenceNumber = 2),
                MatchingPairEntity(exerciseId = ex9Id, leftItem = "Furthermore", rightItem = "in addition", sequenceNumber = 3),
                MatchingPairEntity(exerciseId = ex9Id, leftItem = "Nevertheless", rightItem = "unexpected contrast", sequenceNumber = 4)
            )
        )
    }
}
