
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

val webClientId = gradleLocalProperties(rootDir, providers).getProperty("web_client_id", "{add web-client-id from firebase to make authentication work}")

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.kotlinx.datetime)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.touchlab.kermit)
        }
    }
}

android {
    namespace = "com.malakiapps.whatsappclone"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
        resValue(type = "string", name = "web_client_id", value = webClientId)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.viewmodel)
    implementation(libs.koin.viewmodel.navigation)

    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services)
    implementation(libs.firebase.identity.googleid)

    implementation(libs.room.runtime)
    implementation(libs.room.roomKtx)
    implementation(libs.androidx.core.ktx)
    ksp(libs.room.compiler)
}