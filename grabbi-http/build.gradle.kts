plugins {
    id("punkhomov.grabbi.module")
}

dependencies {
    compileOnly(project(":grabbi-core"))
    compileOnly(libs.ktor.client.core)
}