import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.chaquo.python")
}

val apiKey: String = gradleLocalProperties(rootDir, providers).getProperty("API_KEY", "")
val apiUrlArs: String = gradleLocalProperties(rootDir, providers).getProperty("API_URL_ARS", "")
val apiUrlUsd: String = gradleLocalProperties(rootDir, providers).getProperty("API_URL_USD", "")
val apiUrlBrl: String = gradleLocalProperties(rootDir, providers).getProperty("API_URL_BRL", "")

android {
    namespace = "com.example.reconocimiento_billetes"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.reconocimiento_billetes"
        minSdk = 21 // Version minima de android sdk que se soporta
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        resValue(
            "string",
            "API_KEY",
            "\"" + apiKey + "\""
        )
        resValue(
            "string",
            "API_URL_USD",
            "\"" + apiUrlUsd + "\""
        )
        resValue(
            "string",
            "API_URL_ARS",
            "\"" + apiUrlArs + "\""
        )
        resValue(
            "string",
            "API_URL_BRL",
            "\"" + apiUrlBrl + "\""
        )

        ndk {
            abiFilters += listOf(
                "arm64-v8a",
                "armeabi-v7a",
                "x86_64"
            ) // Para agregar compatibilidad con arquitecturas de dispositivos moviles
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        mlModelBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

chaquopy {
    defaultConfig {
        buildPython("C:\\Users\\maty\\AppData\\Local\\Programs\\Python\\Python311\\python.exe") // Ubicacion de python
        version = "3.8"

        // Para instalar librerias de Python
        pip {
            install("requests")
        }
    }

    sourceSets {
        getByName("main") {
            srcDir("src/main/python") // Ubicacion de los archivos python
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.accompanist.pager)
    implementation(libs.androidx.core.splashscreen)

    // Dependencias para camerax
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)

    // Dependencias para usar el analizador de imagenes
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}