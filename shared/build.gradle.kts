import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvmToolchain(17)
    // 1. Hata Buradaydı: jvmTarget ortak (common) ayarlarda tanımlanamaz. 
    // Sadece androidTarget içinde tanımlanmalıdır.

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    js(IR) {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.core)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.json)
            implementation(libs.ktor.auth)
            implementation(libs.ktor.logging)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.sqldelight.runtime)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.ktor.cio)
            implementation(libs.sqldelight.android)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.security.crypto)
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.ktor.js)
                implementation(libs.sqldelight.web)
            }
        }
    }
}

android {
    namespace = "com.eduplatform"
    compileSdk = 35
    buildToolsVersion = "35.0.0"
    
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

sqldelight {
    databases {
        create("EduDatabase") {
            packageName.set("com.eduplatform.db")
        }
    }
}
