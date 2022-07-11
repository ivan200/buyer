plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("realm-android")
}

android {
    compileSdk = 32
    buildToolsVersion = "32.0.0"
    defaultConfig {
        applicationId = "app.simple.buyer"
        minSdk = 16
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
        ndk {
            //realm does not support armeabi-v6l or armeabi
            abiFilters.addAll(listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a"))
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "$project.rootDir/tools/proguard-rules.pro")
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        targetCompatibility(1.8)
        sourceCompatibility(1.8)
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    //appcompat
    implementation ("androidx.appcompat:appcompat:1.4.2")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")

    //Material 3
    implementation ("com.google.android.material:material:1.6.1")

    //Navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.5.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.0")
    
    //Logging
    implementation ("ch.acra:acra-mail:5.3.0")
    implementation ("ch.acra:acra-toast:5.3.0")

    //View model
    implementation ("androidx.lifecycle:lifecycle-common-java8:2.5.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")

    //Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    //multidex
    implementation("androidx.multidex:multidex:2.0.1")

    //tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("io.mockk:mockk:1.12.4")
}
