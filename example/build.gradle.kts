plugins {
    kotlin("jvm")
}

group = "punkhomov.grabbi"
version = "1.0.0-alpha-1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(exampleLibs.ktor.client.apache5)
    implementation(exampleLibs.jsoup)
    implementation(exampleLibs.gson)

    implementation(project(":grabbi-core"))
    implementation(project(":grabbi-batch"))
    implementation(project(":grabbi-http"))

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}