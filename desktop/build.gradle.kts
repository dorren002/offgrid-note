plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.compose")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

dependencies {
    implementation(project(":common"))
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "com.offgrid.note.MainKt"
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            packageName = "OffgridNote"
            packageVersion = "1.0.0"
        }
    }
}