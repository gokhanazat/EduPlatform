package com.eduplatform.android

import android.app.Application
import com.eduplatform.di.androidModule
import com.eduplatform.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EduPlatformApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EduPlatformApp)
            modules(sharedModule, androidModule)
        }
    }
}
