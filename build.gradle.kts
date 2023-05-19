val orgId: String by project
val moduleId: String by project
val versionNumber: String = System.getenv("VERSION_NUMBER").orEmpty().ifEmpty { "0.0.0" }
val buildTag: String = System.getenv("BUILD_TAG").orEmpty().ifEmpty { "0.0.0-dev" }
val isDevelopment: Boolean = project.ext.has("development")
val osName: String = System.getProperty("os.name").lowercase()
val tomcatNativeOSClassifier: String? = when {
  osName.contains("win") -> "windows-x86_64"
  osName.contains("linux") -> "linux-x86_64"
  osName.contains("mac") -> "osx-x86_64"
  else -> null
}

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val postgresVersion: String by project
val h2Version: String by project
val prometeusVersion: String by project
val tomcatNativeVersion: String by project

group = "com.${orgId}" // "com.etelie"
version = buildTag     // "{version}-{build}"

plugins {
  kotlin("jvm") version "1.8.10"
  id("io.ktor.plugin") version "2.2.4"
  id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
//  id("com.palantir.docker") version "0.34.0"
}

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

  // Netty/TomcatNative
  implementation(
    if (tomcatNativeOSClassifier != null) {
      "io.netty:netty-tcnative-boringssl-static:$tomcatNativeVersion:$tomcatNativeOSClassifier"
    } else {
      "io.netty:netty-tcnative-boringssl-static:$tomcatNativeVersion"
    }
  )

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
  implementation("io.ktor:ktor-network-tls-certificates:$ktorVersion")

  // Test
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
  testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
}

//docker {
//  project.version.toString().isEmpty().ifTrue {
//    throw Exception("Project version not found")
//  }
//
//  val repository: String = "${orgId}/${moduleId}"
//  val tag: String = project.version.toString()
//
//  name = "${repository}:${tag}"
//  setDockerfile(File("./docker/deploy/Dockerfile"))
//  files(fileTree("./build/libs/"))
//  buildArgs(
//    mapOf(
//      "BUILD_TAG" to buildTag
//    )
//  )
//  noCache(true)
//}
