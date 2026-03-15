package com.eduplatform.di

import com.eduplatform.data.api.*
import com.eduplatform.data.repository.*
import com.eduplatform.domain.repository.*
import com.eduplatform.presentation.viewmodel.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    single { createHttpClient(get()) }

    singleOf(::AuthApiService)
    singleOf(::CourseApiService)
    singleOf(::LessonApiService)
    singleOf(::QuizApiService)
    singleOf(::CertificateApiService)

    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<CourseRepository> { CourseRepositoryImpl(get(), get()) }
    single<LessonRepository> { LessonRepositoryImpl(get(), get()) }
    single<QuizRepository> { QuizRepositoryImpl(get(), get()) }
    single<CertificateRepository> { CertificateRepositoryImpl(get()) }

    factory { AuthViewModel(get()) }
    factory { CourseViewModel(get()) }
    factory { LessonViewModel(get()) }
    factory { QuizViewModel(get()) }
    factory { CertViewModel(get()) }
    factory { AdminViewModel(get(), get()) } // YENİ: AdminViewModel eklendi
}
