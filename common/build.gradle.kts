plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("com.squareup.sqldelight")
}

kotlin {
    jvmToolchain(17)
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                api("com.squareup.sqldelight:runtime:1.5.5")
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api("org.jetbrains.compose.material:material-icons-extended:1.5.11")
            }
        }
        val jvmMain by getting {
            dependencies {
                api("com.squareup.sqldelight:sqlite-driver:1.5.5")
            }
        }
    }
}

sqldelight {
    database("OffgridDatabase") {
        packageName = "com.offgrid.note.database"
    }
}