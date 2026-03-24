plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.eduplatform.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    kotlinOptions {
        jvmTarget = "17"
    }
    defaultConfig {
        applicationId = "com.eduplatform.android"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    
    sourceSets {
        getByName("main") {
            res.srcDirs(
                "src/main/res"
                // Çakışmaya neden olan "../composeApp/src/androidMain/res" kaldırıldı.
                // Kaynaklar zaten androidApp içinde mevcut.
            )
        }
    }
    
    buildToolsVersion = "35.0.0"

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.cmp.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coil.compose)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.markdown.renderer.android)
    implementation(libs.zxing.core)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.compose)
    implementation(libs.koin.core)
    debugImplementation(libs.cmp.ui.tooling)
}
