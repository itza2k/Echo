import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // SQLDelight Android Driver
            implementation(libs.sqldelight.android.driver)

            // Ktor Android Client
            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // KotlinX
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.sqldelight.primitive.adapters)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            // SQLDelight SQLite Driver
            implementation(libs.sqldelight.sqlite.driver)

            // Ktor Java Client
            implementation(libs.ktor.client.java)
        }
    }
}

android {
    namespace = "org.itza2k.echo"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.itza2k.echo"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.itza2k.echo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.itza2k.echo"
            packageVersion = "1.0.0"
        }
    }
}

sqldelight {
    databases {
        create("EkoDatabase") {
            packageName.set("org.itza2k.echo.data.db")
        }
    }
}

// Task to fix syntax issues in SQLDelight generated files
tasks.register("fixSqlDelightGeneratedFiles") {
    group = "sqldelight"
    description = "Fixes syntax issues in SQLDelight generated files"

    // This task should run after SQLDelight generates the code
    dependsOn("generateCommonMainEkoDatabaseInterface")

    doLast {
        val generatedDir = File(buildDir, "generated/sqldelight/code/EkoDatabase/commonMain/org/itza2k/echo/data/db")
        if (generatedDir.exists()) {
            generatedDir.walkTopDown().filter { it.isFile && it.extension == "kt" }.forEach { file ->
                var content = file.readText()

                // Fix 1: Replace trailing commas in parameter lists
                content = content.replace(Regex(",(\\s*\\))"), "$1")

                // Fix 2: Fix incorrect imports for Boolean
                content = content.replace(
                    "import Boolean", 
                    "import kotlin.Boolean"
                )

                // Write the fixed content back to the file
                file.writeText(content)
                println("Fixed syntax issues in ${file.name}")
            }
        }
    }
}

// Make sure our fix task runs after code generation
// Hook into the compilation tasks for all targets
afterEvaluate {
    tasks.matching { task ->
        task.name.startsWith("compile") && task.name.contains("Kotlin") && !task.name.contains("Test")
    }.configureEach {
        dependsOn("fixSqlDelightGeneratedFiles")
    }
}
