import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.roborazzi.plugin)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.dayn.forday"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dayn.forday"
        minSdk = 28
        targetSdk = 36
        versionCode = 4
        versionName = "1.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KAKAO_API_KEY", getProperty("KAKAO_API_KEY"))
        resValue("string", "KAKAO_REDIRECT_URI", getProperty("KAKAO_REDIRECT_URI"))
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file(getProperty("KEYSTORE"))
            storePassword = "wony2401"
//            storePassword = getProperty("KEYSTORE_PASSWORD")
            keyAlias = getProperty("KEY_ALIAS")
//            keyPassword = getProperty("KEY_PASSWORD")
            keyPassword = "wony2401"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", getProperty("BASE_URL_PROD"))
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField("String", "BASE_URL", getProperty("BASE_URL_DEV"))
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
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

fun getProperty(key: String): String {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        FileInputStream(localPropertiesFile).use(properties::load)
    }
    return properties.getProperty(key)
        ?: throw IllegalStateException("'$key' not found in local.properties")
}

dependencies {
    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.appcompat)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation("androidx.compose.material:material")
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.navigation)

    // Navigation3
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // Network
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)

    // Kotlinx
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.immutable)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.okhttp)

    // UI Effects
    implementation(libs.compose.shimmer)

    // Firebase
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseCrashlytics)
    implementation(libs.firebaseConfig)
    implementation(libs.firebase.analytics)

    // DataStore
    implementation(libs.androidx.datastore)

    // Glance (App Widgets)
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)

    // Profile Installer
    implementation(libs.androidx.profileinstaller)

    // OSS Licenses
    implementation(libs.oss.licenses)

    // Desugar JDK
    coreLibraryDesugaring(libs.android.desugarJdkLibs)

    // Testing - JUnit 4
    testImplementation(libs.junit4)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.coroutines.test)

    // Testing - JUnit 5
    testImplementation(libs.test.junit5)
    testRuntimeOnly(libs.test.junit5.engine)

    // Testing - Kotest
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)

    // Testing - Mockito
    testImplementation(libs.test.mockito)
    testImplementation(libs.test.mockito.kotlin)

    // Android Testing
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.androidx.compose.navigation.test)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)

    // Screenshot Testing (Roborazzi)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazziRule)
    testImplementation(libs.roborazziCompose)

    // Kakao
    implementation(libs.kakao.user)
    implementation(libs.kakao.share)

    // Timber
    implementation(libs.timber)

    implementation(libs.lottie.compose)
}