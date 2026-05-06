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
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                implementation("com.squareup.sqldelight:runtime:1.5.5")
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation("org.jetbrains.compose.material:material-icons-extended:1.5.11")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:sqlite-driver:1.5.5")
            }
        }
    }
}

sqldelight {
    database("OffgridDatabase") {
        packageName = "com.offgrid.note.database"
    }
}