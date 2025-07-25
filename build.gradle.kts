val orgId: String by project
val moduleId: String by project
val mainClassName: String by project

val ktorVersion: String by project
val kotlinVersion: String by project
val kotlinxVersion: String by project
val logbackVersion: String by project
val postgresqlVersion: String by project
val tomcatNativeVersion: String by project
val exposedVersion: String by project
val hikariCPVersion: String by project
val newRelicVersion: String by project
val openTelemetryVersion: String by project
val openTelemetryInstrumentationVersion: String by project
val tegralVersion: String by project
val quartzVersion: String by project
val kotlinLoggingVersion: String by project
val http4kVersion: String by project
val awsSdkVersion: String by project
val mockkVersion: String by project
val junitVersion: String by project

val localFlywayUrl: String by project
val localFlywayUser: String by project
val localFlywayPassword: String by project

val versionNumber = System.getenv("VERSION_NUMBER").orEmpty().ifEmpty { "0.0.0" }
val buildTag = System.getenv("BUILD_TAG").orEmpty().ifEmpty { "0.0.0-dev" }

/** "production" | "staging" | "test" | "development" **/
val environmentLabel = System.getenv("EXECUTION_ENVIRONMENT").orEmpty().ifEmpty { "development" }
val newRelicLicenseKey = System.getenv("NEWRELIC_LICENSE_KEY").orEmpty()
val serverPort = System.getenv("SERVER_PORT").orEmpty().ifEmpty { "402" }.run { toInt() }

val tomcatNativeOSClassifier = System.getProperty("os.name").orEmpty().lowercase().run {
    when {
        contains("win") -> "windows-x86_64"
        contains("linux") -> "linux-x86_64"
        contains("mac") -> "osx-x86_64"
        else -> null
    }
}
val newRelicJar = projectDir.resolve("opt/newrelic/newrelic.jar")
val openTelemetryJar = projectDir.resolve("opt/opentelemetry/opentelemetry-javaagent.jar")
val openTelemetryProperties = projectDir.resolve("src/main/resources/opentelemetry.properties")

fun isDeployed(environmentLabel: String): Boolean {
    val deployableEnvironments = setOf("production", "staging")
    return deployableEnvironments.contains(environmentLabel)
}

fun getJvmArgs(environmentLabel: String) = mutableSetOf(
    "-Dio.ktor.development=${environmentLabel == "development"}",
    "-Dnewrelic.environment=$environmentLabel",
    "-Dnewrelic.config.license_key=$newRelicLicenseKey",
    "-Dotel.javaagent.configuration-file=${openTelemetryProperties.absolutePath}",
    "-Dotel.service.name=$moduleId-$environmentLabel",
).apply {
    if (!isDeployed(environmentLabel)) return@apply
    add("-javaagent:${newRelicJar.absolutePath}")
    add("-javaagent:${openTelemetryJar.absolutePath}")
}

group = "com.${orgId}" // "com.etelie"
version = buildTag // "{version}-{build}"

repositories {
    mavenCentral()
    google()
}

plugins {
    kotlin("jvm") version "1.8.10"
    id("io.ktor.plugin") version "2.2.4"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    id("org.flywaydb.flyway") version "9.8.1"
}

application {
    mainClass.set(mainClassName)
    applicationDefaultJvmArgs = getJvmArgs(environmentLabel)
}

ktor {
    fatJar {
        archiveFileName.set("com.$orgId.$moduleId-$buildTag.jar")
    }
}

jib {
    containerizingMode = "packaged"
}

flyway {
    url = System.getProperty("etelie.flyway.url") ?: localFlywayUrl
    user = System.getProperty("etelie.flyway.user") ?: localFlywayUser
    password = System.getProperty("etelie.flyway.password") ?: localFlywayPassword
    table = "changelog"
}

tasks.test {
    useJUnitPlatform()
}

tasks.distTar {
    dependsOn(tasks.shadowJar)
}

tasks.distZip {
    dependsOn(tasks.shadowJar)
}

tasks.startScripts {
    dependsOn(tasks.shadowJar)
}

tasks.startShadowScripts {
    dependsOn(tasks.jar, tasks.shadowJar)
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", kotlinxVersion)

    // Persistence (General)
    implementation("org.postgresql", "postgresql", postgresqlVersion)
    implementation("com.zaxxer", "HikariCP", hikariCPVersion)

    // Exposed
    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-kotlin-datetime", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-money", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-crypt", exposedVersion)

    // Monitoring (General)
    implementation("io.ktor", "ktor-server-metrics-micrometer", ktorVersion)
    implementation("ch.qos.logback", "logback-classic", logbackVersion)
    implementation("io.github.oshai", "kotlin-logging-jvm", kotlinLoggingVersion)

    // OpenTelemetry
    implementation(platform("io.opentelemetry:opentelemetry-bom:$openTelemetryVersion"))
    implementation("io.opentelemetry", "opentelemetry-api")
    implementation("io.opentelemetry", "opentelemetry-sdk")
    implementation("io.opentelemetry", "opentelemetry-exporter-otlp")
    implementation("io.opentelemetry", "opentelemetry-extension-kotlin")

    // Netty/TomcatNative
    implementation(
        if (tomcatNativeOSClassifier != null) {
            "io.netty:netty-tcnative-boringssl-static:$tomcatNativeVersion:$tomcatNativeOSClassifier"
        } else {
            "io.netty:netty-tcnative-boringssl-static:$tomcatNativeVersion"
        },
    )

    // Ktor
    implementation("io.ktor", "ktor-server-core-jvm", ktorVersion)
    implementation("io.ktor", "ktor-serialization-kotlinx-json-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-content-negotiation-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-metrics-micrometer-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-metrics-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-call-logging-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-call-id-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-http-redirect-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-hsts-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-default-headers-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-caching-headers-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-resources", ktorVersion)
    implementation("io.ktor", "ktor-server-auto-head-response-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-auth-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-netty-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-cors", ktorVersion)
    implementation("io.ktor", "ktor-network-tls-certificates", ktorVersion)
    implementation("io.ktor", "ktor-server-rate-limit", ktorVersion)
    implementation("io.ktor", "ktor-server-status-pages", ktorVersion)
    implementation("io.ktor", "ktor-server-config-yaml", ktorVersion)

    // Tegral
    implementation(tegralLibs.core)
    implementation(tegralLibs.openapi.dsl)
    implementation(tegralLibs.openapi.ktor)

    // Http4k
    implementation(platform("org.http4k:http4k-bom:$http4kVersion"))
    implementation("org.http4k", "http4k-core")
    implementation("org.http4k", "http4k-client-apache")
    implementation("org.http4k", "http4k-client-apache-async")

    // AWS
    implementation("aws.sdk.kotlin", "secretsmanager", awsSdkVersion)
    implementation("aws.sdk.kotlin", "rds", awsSdkVersion)

    // Miscellaneous
    implementation("org.quartz-scheduler", "quartz", quartzVersion)

    // Test
    testImplementation("org.jetbrains.kotlin", "kotlin-test", kotlinVersion)
    testImplementation("io.ktor", "ktor-server-test-host", ktorVersion)
    testImplementation("io.ktor", "ktor-server-tests-jvm", ktorVersion)
    testImplementation("io.mockk", "mockk", mockkVersion)
    testImplementation("org.junit.jupiter", "junit-jupiter-params", junitVersion)
}
