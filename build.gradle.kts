// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")

        // https://mvnrepository.com/artifact/io.realm/realm-gradle-plugin
        classpath("io.realm:realm-gradle-plugin:10.11.1")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

val minSdkVersion by rootProject.extra { 16 }
val targetSdkVersion by rootProject.extra { 32 }
val compileSdkVersion by rootProject.extra { 32 }
val buildToolsVersion by rootProject.extra { "32.0.0" }
