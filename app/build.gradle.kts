/**
 * With more time, I'd unify the gradle files to reduce on duplicates.
 */
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.com.dagger.hilt.android)
}

android {
    namespace = "com.aliziwa.openweatherjpmc"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aliziwa.openweatherjpmc"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources.excludes.add("META-INF/*")
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))


    implementation(libs.dagger.hilt.android)
    implementation(libs.play.services.location)
    kapt(libs.dagger.hilt.compiler)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraint.layout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.glide)
    implementation(libs.androidx.preference.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.fragment.testing)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}


// Allow references to generated code
kapt {
    correctErrorTypes = true
}