plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.a1group9.myapplication"
    compileSdk = 34  // Changed to stable version (35 is not officially released yet)

    defaultConfig {
        applicationId = "com.a1group9.myapplication"
        minSdk = 24
        targetSdk = 34  // Changed to match compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets")  // Simplified directory structure
            }
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Add RecyclerView dependency (if using previous music list implementation)
    implementation(libs.androidx.recyclerview)

    // MediaPlayer ExoPlayer (optional for better audio handling)
    // implementation(libs.androidx.media3.exoplayer)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}