plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.example"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.aistudio.iptvplayer.kdyzrw"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {

        release {
            isCrunchPngs = false
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }


        debug {
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }


    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        // Match compiler extension to Compose BOM; 1.6.0 is compatible with recent BOMs
        kotlinCompilerExtensionVersion = "1.6.0"
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}



secrets {

    propertiesFileName = ".env"

    defaultPropertiesFileName = ".env.example"

}


dependencies {


    implementation(platform(libs.androidx.compose.bom))


    implementation(libs.androidx.activity.compose)

    implementation("io.coil-kt:coil-compose:2.7.0")

   implementation("androidx.datastore:datastore-preferences:1.1.1")
    
  // Compose UI

    implementation(libs.androidx.compose.material.icons.core)

    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.compose.ui)

    implementation(libs.androidx.compose.ui.graphics)

    implementation(libs.androidx.compose.ui.tooling.preview)



    implementation(libs.androidx.core.ktx)


    // Lifecycle

    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.lifecycle.viewmodel.compose)



    // Images

    implementation(libs.coil.compose)


    // Media Player IPTV

    implementation(libs.androidx.media3.exoplayer)

    implementation(libs.androidx.media3.ui)

    implementation("androidx.media3:media3-exoplayer-hls:1.6.1")



    // Network

    implementation(libs.retrofit)

    implementation(libs.converter.moshi)

    implementation(libs.okhttp)

    implementation(libs.logging.interceptor)


    implementation(libs.moshi.kotlin)


    // Coroutines

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.kotlinx.coroutines.core)


    // Room Database
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    ksp("androidx.room:room-compiler:2.7.0")



    // Moshi Code Generation

    ksp(libs.moshi.kotlin.codegen)


    // Tests

    testImplementation(libs.androidx.compose.ui.test.junit4)

    testImplementation(libs.androidx.core)

}