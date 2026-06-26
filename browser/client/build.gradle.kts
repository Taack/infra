//plugins {
//    kotlin("multiplatform")  version "2.4.0"
////    id "org.jetbrains.kotlin.multiplatform" version "1.9.23"
////    id "org.jetbrains.kotlin.plugin.serialization" version "1.9.23"
//
//}

//import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
//    alias(libs.plugins.composeMultiplatform)
//    alias(libs.plugins.composeCompiler)
}

group = "org.taack"
version = "1.0-SNAPSHOT"

kotlin {
    js {
        browser()
        binaries.executable()
    }

//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//        binaries.executable()
//    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser-js:2026.6.6")
            }
        }
    }
}
