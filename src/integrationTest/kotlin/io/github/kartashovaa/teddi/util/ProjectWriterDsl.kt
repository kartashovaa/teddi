package io.github.kartashovaa.teddi.util

import io.github.kartashovaa.teddi.util.*
import org.intellij.lang.annotations.Language
import java.io.File

fun GradleProjectWriter.createMinimalRootProject(agpVersion: String = "7.3.0") {
    settingsScript(DEFAULT_SETTINGS_SCRIPT)
    buildScript(ANDROID_ROOT_BUILD_SCRIPT)
    properties("android.useAndroidX=true")
}

fun TeddiProjectWriter.createMinimalSubProject(name: String): TeddiProjectWriter {
    File(projectDir, "settings.gradle").appendText("\ninclude ':$name'")
    return subproject(name)
}

fun TeddiProjectWriter.createAndroidApplicationModule(
    name: String = "app",
    dependencies: List<String> = emptyList()
): TeddiProjectWriter {
    return createMinimalSubProject(name).apply {
        buildScript(createApplicationBuildScript(dependencies))
        manifest(DEFAULT_MANIFEST)
    }
}

fun TeddiProjectWriter.createAndroidLibraryModule(
    name: String = "feature",
    dependencies: List<String> = emptyList()
): TeddiProjectWriter {
    return createMinimalSubProject(name).apply {
        buildScript(createLibraryBuildScript(dependencies))
        manifest(DEFAULT_MANIFEST)
    }
}

fun TeddiProjectWriter.createKotlinModule(
    name: String = "feature",
): TeddiProjectWriter {
    return createMinimalSubProject(name).apply {
        buildScript("""
            plugins {
                id 'kotlin'
            }
            dependencies {
                testImplementation 'junit:junit:4.13.2'
            }
        """.trimIndent())
    }
}

@Language("groovy")
private val DEFAULT_SETTINGS_SCRIPT = """
    pluginManagement {
        repositories {
            mavenLocal()
            google() 
            gradlePluginPortal()
        }
    }
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
        }
    }
    rootProject.name = "TeddiSandbox"
""".trimIndent()

@Language("groovy")
private val ANDROID_ROOT_BUILD_SCRIPT = """
    plugins {
        id 'com.android.application' version "7.3.0" apply false
        id 'com.android.library' version "7.3.0" apply false
        id 'org.jetbrains.kotlin.android' version '1.8.0' apply false
        id 'io.github.kartashovaa.teddi' version 'latest.integration'
    }

""".trimIndent()

@Language("groovy")
private fun createApplicationBuildScript(dependencies: List<String>) = """
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.example.app'
    compileSdk 33

    defaultConfig {
        applicationId "com.example.app"
        minSdk 24
        targetSdk 33
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion . VERSION_1_8
                targetCompatibility JavaVersion . VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.8.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    ${dependencies.joinToString(separator = "\n") { "implementation project(':$it')" }} 
    
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
""".trimIndent()
@Language("groovy")
private fun createLibraryBuildScript(dependencies: List<String>) = """
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.example.library'
    compileSdk 33

    defaultConfig {
        minSdk 24
        targetSdk 33
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
                targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.8.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    ${dependencies.joinToString(separator = "\n") { "implementation project(':$it')" }} 
    
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
""".trimIndent()


@Language("xml")
private val DEFAULT_MANIFEST = """
    <?xml version="1.0" encoding="utf-8"?>
    <manifest />
""".trimIndent()

@Language("kt")
val DEFAULT_VIEW_MODEL_CONTENT = """
            import androidx.lifecycle.ViewModel

            class MainViewModel: ViewModel() {

                fun doSomething() {
                    
                }
            }
        """.trimIndent()
@Language("kt")
val OTHER_VIEW_MODEL_CONTENT = """
            import androidx.lifecycle.ViewModel

            class OtherViewModel: ViewModel() {

                fun doSomething() {
                    
                }
            }
        """.trimIndent()

@Language("kt")
val DEFAULT_VIEWMODEL_TEST_CONTENT = """
    import org.junit.Assert.assertTrue
    import org.junit.Test

    class MainViewModelTest {

        private val viewModel = MainViewModel()

        @Test
        fun successTest() {
            viewModel.doSomething()

            assertTrue(true)
        }
    }
""".trimIndent()
@Language("kt")
val DEFAULT_OTHER_VIEWMODEL_TEST_CONTENT = """
    import org.junit.Assert.assertTrue
    import org.junit.Test

    class OtherViewModelTest {

        private val viewModel = OtherViewModel()

        @Test
        fun successTest() {
            viewModel.doSomething()

            assertTrue(true)
        }
    }
""".trimIndent()
