/**
 * With more time, I'd unify the gradle files to reduce on duplicates.
 */
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.com.dagger.hilt.android)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.com.google.protobuf)
}

protobuf {

    protoc {
        artifact = "com.google.protobuf:protoc:3.19.4"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("kotlin")
                create("java")
            }
        }
    }
}

android {
    namespace = "com.aliziwa.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)
    kapt(libs.moshi.codegen)

    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.codegen)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapters)
    implementation(libs.core.ktx)
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.kotlin)

    androidTestImplementation(libs.espresso.core)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
