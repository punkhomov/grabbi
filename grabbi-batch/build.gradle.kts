plugins {
    id("punkhomov.grabbi.module")
}

dependencies {
    compileOnly(project(":grabbi-core"))
    compileOnly(libs.kotlinx.coroutines.core)
}