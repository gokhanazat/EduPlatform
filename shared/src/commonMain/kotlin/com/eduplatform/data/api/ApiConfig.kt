package com.eduplatform.data.api

object ApiConfig {
    // BURAYI KENDİ BİLGİLERİNİZLE GÜNCELLEYİN
    const val BASE_URL = "https://kscykywvwdilmfsflkkq.supabase.co"
    const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtzY3lreXd2d2RpbG1mc2Zsa2txIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzM0ODA5MDQsImV4cCI6MjA4OTA1NjkwNH0.mWlLmb4ZTxZFptVxMQsKcc231mazNHpcd5TtY8vC2ew"

    // Supabase PostgREST Endpoints
    const val COURSES = "/rest/v1/courses"
    const val LESSONS = "/rest/v1/lessons"
    const val ENROLLMENTS = "/rest/v1/enrollments"
    const val LESSON_PROGRESS = "/rest/v1/lesson_progress"
    const val QUIZZES = "/rest/v1/quizzes"
    const val QUESTIONS = "/rest/v1/questions"
    const val OPTIONS = "/rest/v1/options"
    const val QUIZ_ATTEMPTS = "/rest/v1/quiz_attempts"
    const val CERTIFICATES = "/rest/v1/certificates"
    const val PROFILES = "/rest/v1/profiles"

    // Supabase Auth Endpoints
    const val SIGN_UP = "/auth/v1/signup"
    const val SIGN_IN = "/auth/v1/token?grant_type=password"
    const val SIGN_OUT = "/auth/v1/logout"
    const val ME = "/auth/v1/user"
    const val REFRESH = "/auth/v1/token?grant_type=refresh_token"
    const val RESET = "/auth/v1/recover"

    // Edge Functions
    const val GEN_PDF = "/functions/v1/generate-certificate"
    const val VERIFY = "/functions/v1/verify-certificate"
}
