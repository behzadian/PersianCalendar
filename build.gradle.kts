plugins {
    id("com.android.application") version "7.1.2" apply false
    id("com.android.library") version "7.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.6.10" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.5.3" apply false
    id("com.google.gms.google-services") version "4.3.10" apply false
    id("com.google.firebase.firebase-perf") version "1.4.1" apply false
    id("com.google.firebase.crashlytics") version "2.8.1" apply false
}

task("clean") {
    delete(rootProject.buildDir)
}
gradle.projectsLoaded {
    rootProject.allprojects {
        buildscript {
            repositories {
                maven("https://gradle.iranrepo.ir")
            }
        }
        repositories {
            maven("https://gradle.iranrepo.ir")
        }
    }
}
