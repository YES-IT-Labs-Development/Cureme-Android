import org.gradle.kotlin.dsl.implementation
import java.util.Properties // 1. Top par import zaroori hai
import java.io.FileInputStream
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")

}



android {
    namespace = "com.bussiness.curemegptapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.bussiness.curemegptapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    val localProperties = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            load(FileInputStream(file))
        }
    }

    // String value ko quotes mein wrap karna zaroori hai
    val baseUrl = localProperties.getProperty("baseUrl") ?: "\"https://curemegpt.tgastaging.com/api/\""

    buildTypes {
        // Release Build Type
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // BASE_URL yahan add karein
            buildConfigField("String", "BASE_URL", baseUrl)
        }

        // Debug Build Type (Testing ke liye)
        getByName("debug") {
            buildConfigField("String", "BASE_URL", baseUrl)
        }
    }

    // 2. BuildConfig generate karne ke liye ye enable karein
    buildFeatures {
        buildConfig = true
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.runtime)

    implementation("androidx.compose.material:material-icons-extended")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Type-safe Navigation
    implementation (libs.androidx.navigation.compose)

    // Kotlinx Serialization (for sealed routes)
    implementation (libs.kotlinx.serialization.json)

    // viewmodel
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    implementation (libs.androidx.lifecycle.runtime.compose)
    implementation (libs.kotlinx.coroutines.android)

    // coil
    implementation (libs.coil.compose)

    implementation(libs.kotlinx.serialization.json)


    // hilt
    implementation (libs.hilt.android)
    kapt (libs.hilt.compiler)
    // Hilt for Jetpack Compose
    implementation (libs.androidx.hilt.navigation.compose)

    implementation (libs.material)

    // speech
    implementation (libs.accompanist.permissions.v0360)

    implementation(libs.lottie.compose)

    // OR latest version
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.1.0")
    implementation ("com.jakewharton.timber:timber:5.0.1")

    implementation("com.vanniktech:android-image-cropper:4.5.0")

    //Retrofit for api
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.10")
    implementation("com.google.code.gson:gson:2.10.1")


    //google Firebase
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material:1.7.8")

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.android.gms:play-services-auth:21.1.1")


//    // firebase
//    implementation(platform(libs.firebase.bom))
//    implementation(libs.firebase.messaging.ktx)
//    implementation(libs.firebase.crashlytics)
//    implementation(libs.firebase.analytics)

}