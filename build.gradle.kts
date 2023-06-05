import io.ktor.plugin.features.*

val orgId: String by project
val moduleId: String by project
val versionNumber: String = System.getenv("VERSION_NUMBER").orEmpty().ifEmpty { "0.0.0" }
val buildTag: String = System.getenv("BUILD_TAG").orEmpty().ifEmpty { "0.0.0-dev" }
val isDevelopment: Boolean = System.getenv("IS_DEVELOPMENT")?.equals("TRUE") ?: false

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val postgresqlVersion: String by project
val h2Version: String by project
val prometeusVersion: String by project
val tomcatNativeVersion: String by project
val exposedVersion: String by project
val hikariCPVersion: String by project

val flywayUrl: String by project
val flywayUser: String by project
val flywayPassword: String by project

val tomcatNativeOSClassifier: String? = System.getProperty("os.name")?.lowercase()?.run {
  when {
    contains("win") -> "windows-x86_64"
    contains("linux") -> "linux-x86_64"
    contains("mac") -> "osx-x86_64"
    else -> null
  }
}

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
  mainClass.set("com.etelie.ApplicationKt")
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktor {
  docker {
    jreVersion.set(JreVersion.JRE_17)
    imageTag.set(buildTag)
    portMappings.set(listOf(
      DockerPortMapping(402, 402, DockerPortMappingProtocol.TCP)
    ))
  }
}

flyway {
  url = System.getenv("FLYWAY_URL") ?: flywayUrl
  user = System.getenv("FLYWAY_USER") ?: flywayUser
  password = System.getenv("FLYWAY_PASSWORD") ?: flywayPassword
  table = "changelog"
}

dependencies {
  // General Persistence
  implementation("org.postgresql", "postgresql", postgresqlVersion)
  implementation("com.zaxxer", "HikariCP", hikariCPVersion)

  // Monitoring
  implementation("io.micrometer", "micrometer-registry-prometheus", prometeusVersion)
  implementation("ch.qos.logback", "logback-classic", logbackVersion)

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

  // Exposed
  implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
  implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
  implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
  implementation("org.jetbrains.exposed", "exposed-kotlin-datetime", exposedVersion)
  implementation("org.jetbrains.exposed", "exposed-money", exposedVersion)
  implementation("org.jetbrains.exposed", "exposed-crypt", exposedVersion)

  // Test
  testImplementation("org.jetbrains.kotlin", "kotlin-test", kotlinVersion)
  testImplementation("io.ktor", "ktor-server-test-host", ktorVersion)
  testImplementation("io.ktor", "ktor-server-tests-jvm", ktorVersion)
}
