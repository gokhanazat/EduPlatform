package com.eduplatform.data.repository

import com.eduplatform.data.api.dto.*
import com.eduplatform.domain.model.*

// --- COURSE MAPPERS ---
fun CourseDto.toDomain(): Course = Course(
    id = id ?: "",
    title = title,
    description = description,
    category = category,
    city = city ?: "Belirtilmemiş",
    instructorName = instructor_name,
    durationMinutes = duration_minutes,
    hasCertificate = has_certificate,
    isPublished = is_published,
    thumbnailUrl = thumbnail_url
)

fun EnrollmentDto.toDomain(): Enrollment = Enrollment(
    userId = user_id,
    courseId = course_id,
    enrolledAt = enrolled_at ?: "",
    progressPercent = progress_percent
)

// --- USER MAPPER ---
fun UserDto.toDomain(role: String, city: String): User = User(
    id = id,
    email = email,
    fullName = user_metadata?.full_name ?: email.substringBefore("@"),
    sicilNo = user_metadata?.sicil_no ?: "",
    city = city,
    role = role
)

// --- CERTIFICATE MAPPER ---
fun CertificateDto.toDomain(): Certificate = Certificate(
    id = id,
    userId = user_id,
    courseId = course_id,
    courseTitle = course_title,
    userName = user_name,
    issuedAt = issued_at,
    verifyCode = verify_code,
    pdfUrl = pdf_url
)

// --- QUIZ MAPPERS ---
fun QuizDto.toDomain(): Quiz = Quiz(
    id = id,
    courseId = course_id,
    passScorePercent = pass_score_percent,
    timeLimitMinutes = time_limit_minutes,
    questions = questions.map { it.toDomain() }
)

fun QuestionDto.toDomain(): Question = Question(
    id = id,
    quizId = quiz_id,
    questionText = question_text,
    type = if (question_type == "true_false") QuestionType.TRUE_FALSE else QuestionType.MULTIPLE_CHOICE,
    orderIndex = order_index,
    options = options.map { it.toDomain() }
)

fun OptionDto.toDomain(): QuestionOption = QuestionOption(
    id = id,
    questionId = question_id,
    optionText = option_text,
    isCorrect = is_correct
)

fun QuizAttemptDto.toDomain(): QuizAttempt = QuizAttempt(
    id = id ?: "",
    userId = user_id,
    quizId = quiz_id,
    score = score,
    passed = passed,
    takenAt = taken_at ?: "",
    answers = answers
)

// --- LESSON MAPPER ---
fun LessonDto.toDomain(): Lesson = Lesson(
    id = id,
    courseId = course_id,
    title = title,
    contentType = content_type,
    contentMarkdown = content_markdown ?: "",
    videoUrl = video_url ?: "",
    orderIndex = order_index
)
