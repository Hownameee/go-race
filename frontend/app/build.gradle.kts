import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
}

val properties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}

fun stringBuildConfigValue(name: String, fallback: String): String {
    val value = properties.getProperty(name)?.trim()?.trim('"') ?: fallback
    return "\"$value\""
}

android {
    namespace = "com.grouprace.gorace"
    compileSdk {
        version = release(36)
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.grouprace.gorace"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", stringBuildConfigValue("API_BASE_URL", "http://10.0.2.2:5000"))
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
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation(project(":feature:tracking"))
    implementation(project(":feature:posts"))
    implementation(project(":feature:auth:register"))
    implementation(project(":feature:auth:login"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:notification"))
    implementation(project(":feature:club"))
    implementation(project(":core:data"))
    implementation(project(":core:sync"))
    implementation(project(":core:network"))
    implementation(project(":core:system"))
    implementation(project(":core:notification"))
    implementation(project(":core:common"))
    implementation(project(":core:navigation"))
    implementation(project(":feature:search"))
    implementation(libs.hilt.android)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    annotationProcessor(libs.hilt.compiler)
    annotationProcessor(libs.androidx.hilt.compiler)
    implementation(project(":feature:records"))
    implementation(project(":feature:map"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
