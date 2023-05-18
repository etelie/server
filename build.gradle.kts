import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

val orgId: String by project
val moduleId: String by project
val versionNumber: String = System.getenv("VERSION_NUMBER").orEmpty().ifEmpty { "0.0.0" }
val buildTag: String = System.getenv("BUILD_TAG").orEmpty().ifEmpty { "0.0.0-dev" }
val isDevelopment: Boolean = project.ext.has("development")

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val postgresVersion: String by project
val h2Version: String by project
val prometeusVersion: String by project

plugins {
  kotlin("jvm") version "1.8.10"
  id("io.ktor.plugin") version "2.2.4"
  id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
  id("com.palantir.docker") version "0.34.0"
}

group = "com.${orgId}" // "com.etelie"
version = buildTag     // "{version}-{build}"
application {
  mainClass.set("com.etelie.ApplicationKt")
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
  mavenCentral()
  google()
}

dependencies {
  implementation("org.postgresql:postgresql:$postgresVersion")
  implementation("com.h2database:h2:$h2Version")
  implementation("io.micrometer:micrometer-registry-prometheus:$prometeusVersion")
  implementation("ch.qos.logback:logback-classic:$logbackVersion")

  // Ktor
  implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
  implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-metrics-micrometer-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-metrics-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-call-id-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-http-redirect-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-hsts-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-default-headers-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-caching-headers-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-resources:$ktorVersion")
  implementation("io.ktor:ktor-server-auto-head-response-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
  implementation("io.ktor:ktor-server-cors:$ktorVersion")

  testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
  testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
}

docker {
  project.version.toString().isEmpty().ifTrue {
    throw Exception("Project version not found")
  }

  val repository: String = "${orgId}/${moduleId}"
  val tag: String = project.version.toString()

  name = "${repository}:${tag}"
  setDockerfile(File("./docker/deploy/Dockerfile"))
  files(fileTree("./build/libs/"))
  buildArgs(
    mapOf(
      "BUILD_TAG" to buildTag
    )
  )
  noCache(true)
}
