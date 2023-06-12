import io.ktor.plugin.features.DockerPortMapping
import io.ktor.plugin.features.DockerPortMappingProtocol
import io.ktor.plugin.features.JreVersion

val orgId: String by project
val moduleId: String by project

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val postgresqlVersion: String by project
val h2Version: String by project
val prometeusVersion: String by project
val tomcatNativeVersion: String by project
val exposedVersion: String by project
val hikariCPVersion: String by project
val newRelicVersion: String by project
val openTelemetryVersion: String by project
val openTelemetryInstrumentationVersion: String by project

val localFlywayUrl: String by project
val localFlywayUser: String by project
val localFlywayPassword: String by project

val versionNumber = System.getenv("VERSION_NUMBER").orEmpty().ifEmpty { "0.0.0" }
val buildTag = System.getenv("BUILD_TAG").orEmpty().ifEmpty { "0.0.0-dev" }

/** "production" | "staging" | "test" | "development" **/
val executionEnvironment = System.getenv("EXECUTION_ENVIRONMENT").orEmpty().ifEmpty { "development" }
val newRelicLicenseKey = System.getenv("NEW_RELIC_LICENSE_KEY").orEmpty()

val tomcatNativeOSClassifier = System.getProperty("os.name").orEmpty().lowercase().run {
    when {
        contains("win") -> "windows-x86_64"
        contains("linux") -> "linux-x86_64"
        contains("mac") -> "osx-x86_64"
        else -> null
    }
}
val newRelicJar = projectDir.resolve("newrelic/newrelic.jar")
val openTelemetryJar = projectDir.resolve("opentelemetry/opentelemetry-javaagent.jar")
val deployableEnvironments = setOf("production", "staging")

group = "com.${orgId}" // "com.etelie"
version = buildTag     // "{version}-{build}"

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
    val openTelemetryProperties = projectDir.resolve("src/main/resources/opentelemetry.properties")
    val isDeployed = deployableEnvironments.contains(executionEnvironment)
    val jvmArgs = mutableSetOf(
        "-Dio.ktor.development=${!isDeployed}",
        "-Dnewrelic.environment=$executionEnvironment",
        "-Dnewrelic.config.license_key=$newRelicLicenseKey",
        "-Dotel.javaagent.configuration-file=${openTelemetryProperties.absolutePath}",
        "-Dotel.resource.attributes=service.name=server-$executionEnvironment",
    ).apply {
        if (!isDeployed) return@apply
        add("-javaagent:${newRelicJar.absolutePath}")
        add("-javaagent:${openTelemetryJar.absolutePath}")
    }

    mainClass.set("com.etelie.ApplicationKt")
    applicationDefaultJvmArgs = jvmArgs
}

ktor {
    docker {
        jreVersion.set(JreVersion.JRE_17)
        imageTag.set(buildTag)
        portMappings.set(
            listOf(
                DockerPortMapping(402, 402, DockerPortMappingProtocol.TCP),
            ),
        )
    }
}

flyway {
    url = System.getenv("FLYWAY_URL") ?: localFlywayUrl
    user = System.getenv("FLYWAY_USER") ?: localFlywayUser
    password = System.getenv("FLYWAY_PASSWORD") ?: localFlywayPassword
    table = "changelog"
}

dependencies {
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

    // OpenTelemetry
    implementation(platform("io.opentelemetry:opentelemetry-bom:$openTelemetryVersion"))
    implementation("io.opentelemetry", "opentelemetry-api")
    implementation("io.opentelemetry", "opentelemetry-sdk")
    implementation("io.opentelemetry", "opentelemetry-semconv")
    implementation("io.opentelemetry", "opentelemetry-exporter-otlp")
    implementation("io.opentelemetry", "opentelemetry-extension-kotlin")
    implementation("io.opentelemetry.instrumentation", "opentelemetry-ktor-2.0", openTelemetryInstrumentationVersion)

    // Netty/TomcatNative
    implementation(
        if (tomcatNativeOSClassifier != null) {
            "io.netty:netty-tcnative-boringssl-static:$tomcatNativeVersion:$tomcatNativeOSClassifier"
        } else {
            "io.netty:netty-tcnative-boringssl-static:$tomcatNativeVersion"
        }
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

    // Test
    testImplementation("org.jetbrains.kotlin", "kotlin-test", kotlinVersion)
    testImplementation("io.ktor", "ktor-server-test-host", ktorVersion)
    testImplementation("io.ktor", "ktor-server-tests-jvm", ktorVersion)
}
