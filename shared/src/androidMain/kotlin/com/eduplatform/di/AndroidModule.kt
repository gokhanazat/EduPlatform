package com.eduplatform.di

import com.eduplatform.data.api.TokenManager
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.ColumnAdapter
import com.eduplatform.db.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val intAdapter = object : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()
    override fun encode(value: Int): Long = value.toLong()
}

val androidModule = module {
    single { TokenManager(androidContext()) }
    single {
        val driver = AndroidSqliteDriver(EduDatabase.Schema, androidContext(), "edu.db")
        EduDatabase(
            driver = driver,
            CourseEntityAdapter = CourseEntity.Adapter(
                durationMinutesAdapter = intAdapter
                // hasCertificateAdapter ve isPublishedAdapter artık otomatik hallediliyor.
            ),
            EnrollmentEntityAdapter = EnrollmentEntity.Adapter(
                progressPercentAdapter = intAdapter
            ),
            LessonEntityAdapter = LessonEntity.Adapter(
                orderIndexAdapter = intAdapter
            )
        )
    }
}
