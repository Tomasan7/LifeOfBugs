import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.compose") version "1.2.2"
}

group = "me.tomasan7"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "me.tomasan7.lifeofbugs.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "LifeOfBugs"
            packageVersion = "1.0.0"
        }
    }
}