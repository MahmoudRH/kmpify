plugins {
    kotlin("jvm")
}

group = "mahmoud.habib"
version = "unspecified"

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