plugins {
    kotlin("jvm")
}

group = "mahmoud.habib.kmpify"
version = libs.versions.kmpifyVersion.get()

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.coroutinesCore)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}