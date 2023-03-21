import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

val org_id: String by project
val module_id: String by project
val repository_host: String by project
val build_number: String = System.getenv("BUILD_NUMBER").orEmpty().ifEmpty { "dev" }
val build_env: String = System.getenv("BUILD_ENV").orEmpty().ifEmpty { "dev" }
val isDevelopment: Boolean = project.ext.has("development")

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val postgres_version: String by project
val h2_version: String by project
val prometeus_version: String by project

plugins {
    kotlin("jvm") version "1.8.10"
    id("io.ktor.plugin") version "2.2.4"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    id("com.palantir.docker") version "0.34.0"
}

group = "com.etelie"
version = "0.0.1-${build_number}"
application {
    assert(isDevelopment && build_env == "dev")

    mainClass.set("com.etelie.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("com.h2database:h2:$h2_version")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:$ktor_version")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometeus_version")
    implementation("io.ktor:ktor-server-metrics-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-call-id-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-http-redirect-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-hsts-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-caching-headers-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-resources:$ktor_version")
    implementation("io.ktor:ktor-server-auto-head-response-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

docker {
    project.version.toString().isEmpty().ifTrue {
        throw Exception("Project version not found")
    }

    val repository: String = if (isDevelopment)
        "${org_id}/${build_env}/${module_id}" else
        "${repository_host}/${org_id}/${build_env}/${module_id}"
    val tag: String = project.version.toString()

    name = "${repository}:${tag}"
    setDockerfile(File("./docker/deploy/Dockerfile"))
    files(fileTree("./build/libs/"))
    buildArgs(
        mapOf(
            "PROJECT_VERSION" to project.version.toString()
        )
    )
    noCache(true)
}
