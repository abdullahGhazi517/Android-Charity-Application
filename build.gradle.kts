buildscript {
    dependencies {
        classpath ("com.android.tools.build:gradle:8.5.1")
        classpath ("com.google.gms:google-services:4.3.10")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.1" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}