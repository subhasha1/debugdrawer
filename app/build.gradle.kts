import org.codehaus.groovy.runtime.IOGroovyMethods.getText
import org.codehaus.groovy.runtime.ProcessGroovyMethods
import org.codehaus.groovy.runtime.ProcessGroovyMethods.execute
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler

apply {
    plugin("com.android.application")
    plugin("kotlin-android")
    plugin("kotlin-android-extensions")
    plugin("kotlin-kapt")
}

applyFrom("../dependencies.gradle.kts")

val projectConfiguration: ProjectConfiguration by extra

fun gitSha(): String {
    val p = execute("git rev-parse --short HEAD", Array(0, { "" }), project.rootDir)
    p.waitFor()
    if (p.exitValue() != 0)
        throw  RuntimeException(getText(p.errorStream))
    return ProcessGroovyMethods.getText(p).trim()
}


fun gitTimestamp(): String {
    val p = execute("git log -n 1 --format=%at", Array(0, { "" }), rootDir)
    p.waitFor()
    if (p.exitValue() != 0)
        throw  RuntimeException(getText(p.errorStream))
    return ProcessGroovyMethods.getText(p).trim()
}

fun gitCommitMessage(): String {
    val p = execute("git log -n 1 --format=format:%B", Array(0, { "" }), project.rootDir)
    p.waitFor()
    if (p.exitValue() != 0)
        throw  RuntimeException(getText(p.errorStream))
    return ProcessGroovyMethods.getText(p).trim()
}

android {
    setPublishNonDefault(true)
    compileOptions.incremental = false
    compileSdkVersion(projectConfiguration.android.compileSdkVersion)
    buildToolsVersion(projectConfiguration.android.buildToolsVersion)
    defaultConfig {
        minSdkVersion(projectConfiguration.android.minSdkVersion)
        targetSdkVersion(projectConfiguration.android.targetSdkVersion)

        applicationId = "com.androidbolts.debugdrawer"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
        }
        getByName("debug") {
            isDebuggable = true
            buildConfigField("String", "GIT_SHA", "\"${gitSha()}\"")
            buildConfigField("String", "GIT_COMMIT_MESSAGE", "\"${gitCommitMessage()}\"")
            buildConfigField("long", "BUILD_TIMESTAMP", System.currentTimeMillis().toString() + "L")
            buildConfigField("long", "GIT_TIMESTAMP", "${gitTimestamp()}L")
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin")
            java.srcDirs("src/debug/kotlin")
        }
    }

}

kapt {
    generateStubs = true
}

dependencies {
    compile(projectConfiguration.libs.kotlinStdLib)
    compile(projectConfiguration.libs.appCompat)
    compile(projectConfiguration.libs.constraintLayout)
    kapt(projectConfiguration.libs.daggerCompiler)
    compile(projectConfiguration.libs.dagger)
    compile(projectConfiguration.libs.retrofit)
    compile(projectConfiguration.libs.retrofitGsonConverter)

    debugCompile(projectConfiguration.debugLibs.telescopeLib)
    debugCompile(projectConfiguration.debugLibs.stethoLib)
    debugCompile(projectConfiguration.debugLibs.stethoOkHttp)
    debugCompile(projectConfiguration.debugLibs.loggingIterceptor)
    debugCompile(projectConfiguration.debugLibs.scalpelLib)
    debugCompile(projectConfiguration.debugLibs.phonexLib)

    testCompile(projectConfiguration.testLibs.junit)
    testCompile(projectConfiguration.testLibs.mockito)

    androidTestCompile(projectConfiguration.testLibs.espresso) {
        exclude("com.android.support", "support-annotations")
    }
}

inline fun DependencyHandler.debugCompile(dependencyNotation: String) = add("debugCompile", dependencyNotation)

inline fun DependencyHandler.androidTestCompile(
        dependencyNotation: String,
        dependencyConfiguration: ExternalModuleDependency.() -> Unit): ExternalModuleDependency =
        add("androidTestCompile", dependencyNotation, dependencyConfiguration)