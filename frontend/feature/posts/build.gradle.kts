plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.grouprace.feature.posts"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 29

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    
    implementation(project(":core:data"))
    implementation(project(":core:model"))

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Hilt
    implementation(libs.hilt.android)
    annotationProcessor(libs.hilt.compiler)

    implementation(project(":core:common"))
    implementation(project(":core:service"))
    implementation(project(":core:map"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}