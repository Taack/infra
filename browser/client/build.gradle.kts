plugins {
    kotlin("multiplatform")  version "2.1.0-Beta2"
//    id "org.jetbrains.kotlin.multiplatform" version "1.9.23"
//    id "org.jetbrains.kotlin.plugin.serialization" version "1.9.23"

}

group = "org.example"
version = "1.0-SNAPSHOT"

kotlin {
    js(IR) {
//        moduleName = "auo4ever"
        browser {
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                // No need for the js prefix here, you can just copy and paste it from the top-level block
//                implementation("org.jetbrains.kotlinx:kotlinx-html:0.8.0")
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:1.0.0-pre.819")
            }
        }
    }
}

repositories {
    mavenCentral()
}


//rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin) {
//    rootProject.extensions.getByType(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension).lockFileDirectory =
//            file("my-kotlin-js-store")
//    rootProject.extensions.getByType(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension).lockFileName = "my-yarn.lock"
//}
