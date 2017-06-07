class ProjectConfiguration(
        val buildPlugins: BuildPlugins,
        val android: Android,
        val libs: Libs,
        val debugLibs: DebugLibs,
        val testLibs: TestLibs
)

class Android(
        val buildToolsVersion: String,
        val minSdkVersion: Int,
        val targetSdkVersion: Int,
        val compileSdkVersion: Int
)

class BuildPlugins(
        val androidGradle: String,
        val kotlinGradlePlugin: String
)

class Libs(
        val kotlinStdLib: String,
        val appCompat: String,
        val constraintLayout: String,
        val dagger: String,
        val daggerCompiler: String,
        val retrofit: String,
        val retrofitGsonConverter: String
)

class DebugLibs(
        val telescopeLib: String,
        val stethoLib: String,
        val stethoOkHttp: String,
        val scalpelLib: String,
        val phonexLib: String,
        val loggingIterceptor: String
)

class TestLibs(
        val junit: String,
        val mockito: String,
        val espresso: String
)
