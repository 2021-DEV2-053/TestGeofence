plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    buildToolsVersion = Config.BUILD_TOOLS
    compileSdk = Config.COMPILE_SDK

    defaultConfig {
        applicationId = Config.APPLICATION_ID
        minSdk = Config.MIN_SDK
        targetSdk = Config.TARGET_SDK
        versionCode = Config.VERSION_CODE
        versionName = Config.VERSION_NAME
        multiDexEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    packagingOptions {
        resources.excludes.add("**/attach_hotspot_windows.dll")
        resources.excludes.add("META-INF/licenses/**")
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
    }
}

dependencies {
    implementation (Libs.AndroidX.appcompat)
    implementation (Libs.AndroidX.fragment)
    implementation (Libs.Kotlin.stdlib)
    implementation (Libs.Kotlin.Coroutines.android)
    implementation (Libs.AndroidX.Navigation.fragmentKtx)
    implementation (Libs.AndroidX.Navigation.uiKtx)
    implementation (Libs.AndroidX.Lifecycle.viewModelKtx)
    implementation (Libs.AndroidX.Lifecycle.runtimeKtx)

    implementation (Libs.easypermissions)

    implementation (Libs.Google.material)
    implementation (Libs.Google.Maps.playServicesMaps)
    implementation (Libs.Google.Maps.mapsKtx)
    implementation (Libs.Google.Hilt.android)
    kapt (Libs.Google.Hilt.compiler)

}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}