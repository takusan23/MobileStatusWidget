plugins {
    alias(libs.plugins.android.application)
}

android {
    compileSdk {
        version = release(37)
    }
    namespace = "io.github.takusan23.mobilestatuswidget"

    defaultConfig {
        applicationId = "io.github.takusan23.mobilestatuswidget"
        minSdk = 24
        targetSdk = 37
        versionCode = 9
        versionName = "1.6.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // グラフ
    implementation(libs.mpandroidchart)
    // Activity Result API
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    // Coroutine
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}