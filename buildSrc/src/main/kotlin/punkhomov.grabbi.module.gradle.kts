@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.utils.extendsFrom

plugins {
    kotlin("jvm")
}

group = "punkhomov.grabbi"
version = "1.0.0-alpha-1"

repositories {
    mavenCentral()
}

fun libs(alias: String) = versionCatalogs.named("libs").findLibrary(alias).get()

dependencies {
    testImplementation(libs("kotlin-test"))
    testImplementation(libs("kotlinx-coroutines-test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

configurations {
    testImplementation.extendsFrom(compileOnly)
}