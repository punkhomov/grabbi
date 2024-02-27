plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "grabbi"
include("grabbi-core")
include("grabbi-http")
include("grabbi-batch")
include("example")

dependencyResolutionManagement {
    versionCatalogs {
        create("exampleLibs") {
            from(files("gradle/example.versions.toml"))
        }
    }
}