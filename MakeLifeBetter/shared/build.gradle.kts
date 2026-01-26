import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvmToolchain(17)

    androidLibrary {
        namespace = "com.carlosnicolaugalves.makelifebetter.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            // Add this line to all the targets you want to export this dependency
            export(libs.androidx.lifecycle.viewmodel)
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutinesCore)
            api(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            api(libs.androidx.lifecycle.viewmodel)
        }

        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.auth)
            implementation(libs.firebase.firestore)
        }

        iosMain.dependencies {
            api(libs.androidx.lifecycle.viewmodel)
            api(libs.kotlinx.datetime)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.firestore)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
