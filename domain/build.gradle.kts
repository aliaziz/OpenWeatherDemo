/**
 * With more time, I'd unify the gradle files to reduce on duplicates.
 */

plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "com.aliziwa.domain"
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
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.javax.inject)
    implementation(libs.appcompat)
    implementation(libs.material)

    /**
     * Note:
     * These are duplicate in all libraries as a hack to get up and running
     */
    api(libs.junit)
    api(libs.mockk.android)
    api(libs.mockk.agent)
    api(libs.truth)
    api(libs.kotlinx.coroutines.test)
    api(libs.kotlinx.coroutines.core)
    api(libs.espresso.core)
    api(libs.androidx.test.ext.junit)
    api(libs.androidx.core.testing)
}