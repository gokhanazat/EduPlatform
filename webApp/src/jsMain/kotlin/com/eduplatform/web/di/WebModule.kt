package com.eduplatform.web.di

import com.eduplatform.data.api.TokenManager
import org.koin.dsl.module

val webModule = module {
    single<TokenManager> { com.eduplatform.data.api.TokenManager() }
}
