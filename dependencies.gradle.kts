import org.gradle.script.lang.kotlin.extra
import org.gradle.script.lang.kotlin.getValue
import org.gradle.script.lang.kotlin.setValue

var projectConfiguration: ProjectConfiguration by extra

val buildToolsVersion = "25.0.3"
val compileSdkVersion = 25
val minSdkVersion = 16
val targetSdkVersion = 25

// Common
val kotlinVersion = "1.1.2-2"

// Build plugins
val androidGradleVersion = "3.0.0-alpha3"

// Libs
val supportVersion = "25.3.1"
val constraintLayoutVersion = "1.0.2"
val daggerVersion = "2.10"
val okHttpVersion = "3.6.0"
val retrofitVersion = "2.2.0"
//Debug Libs
val telescopeVersion = "2.1.0"
val scalpelVersion = "1.1.2"
val phonexVersion = "1.0.2"
val stethoVersion = "1.4.2"

// Test libs
val junitVersion = "4.12"
val mockitoVersion = "2.7.0"
val espressoVersion = "2.2.2"

projectConfiguration = ProjectConfiguration(
        BuildPlugins(
                "com.android.tools.build:gradle:$androidGradleVersion",
                "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
        ),
        Android(
                buildToolsVersion,
                minSdkVersion,
                targetSdkVersion,
                compileSdkVersion
        ),
        Libs(
                "org.jetbrains.kotlin:kotlin-stdlib-jre7:$kotlinVersion",
                "com.android.support:appcompat-v7:$supportVersion",
                "com.android.support.constraint:constraint-layout:$constraintLayoutVersion",
                "com.google.dagger:dagger:$daggerVersion",
                "com.google.dagger:dagger-compiler:$daggerVersion",
                "com.squareup.retrofit2:retrofit:$retrofitVersion",
                "com.squareup.retrofit2:converter-gson:$retrofitVersion"
        ),
        DebugLibs(
                "com.mattprecious.telescope:telescope:$telescopeVersion",
                "com.facebook.stetho:stetho:$stethoVersion",
                "com.facebook.stetho:stetho-okhttp3:$stethoVersion",
                "com.jakewharton.scalpel:scalpel:$scalpelVersion",
                "com.jakewharton:process-phoenix:$phonexVersion",
                "com.squareup.okhttp3:logging-interceptor:$okHttpVersion"
        ),
        TestLibs(
                "junit:junit:$junitVersion",
                "org.mockito:mockito-core:$mockitoVersion",
                "com.android.support.test.espresso:espresso-core:$espressoVersion"
        )
)