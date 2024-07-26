plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 34
    namespace = "io.github.takusan23.mobilestatuswidget"

    defaultConfig {
        applicationId = "io.github.takusan23.mobilestatuswidget"
        minSdk = 24
        targetSdk = 34
        versionCode = 5
        versionName = "1.2.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // グラフ
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // Activity Result API
    implementation("androidx.activity:activity-ktx:1.9.1")
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}