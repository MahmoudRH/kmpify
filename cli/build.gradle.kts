plugins {
    kotlin("jvm")
    id("org.graalvm.buildtools.native") version "0.10.3"
}

group = "mahmoud.habib.kmpify"
version = libs.versions.kmpifyVersion.get()

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:5.0.2")
    implementation(libs.kotlinx.coroutinesCore)
    api(project(":migration"))
    testImplementation(kotlin("test"))
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("kmpify")
            mainClass.set("mahmoud.habib.kmpify.MainKt")
            buildArgs.add("--no-fallback")
            buildArgs.add("--enable-native-access=ALL-UNNAMED")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}


// Task to build native images for multiple platforms
tasks.register("buildAllNatives") {
    description = "Build native executables for all platforms"
    group = "distribution"
}
