package com.etelie.config

import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.server.engine.ApplicationEngineEnvironmentBuilder
import io.ktor.server.engine.sslConnector
import java.io.File

object TLSConfig {
    const val CERTIFICATE_ALIAS = "etelie_local_tls_cert"
    const val CERTIFICATE_PASSWORD = "certificate"
    const val KEYSTORE_PASSWORD = "keystore"

    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate(CERTIFICATE_ALIAS) {
            password = CERTIFICATE_PASSWORD
            domains = listOf("localhost", "0.0.0.0", "127.0.0.1")
        }
    }

    fun ApplicationEngineEnvironmentBuilder.sslConnector() {
        sslConnector(
            keyStore = keyStore,
            keyAlias = CERTIFICATE_ALIAS,
            keyStorePassword = {KEYSTORE_PASSWORD.toCharArray()},
            privateKeyPassword = {CERTIFICATE_PASSWORD.toCharArray()},
        ) {
            port = 8443
            keyStorePath = keyStoreFile
            keyStore.saveToFile(keyStoreFile, KEYSTORE_PASSWORD)
        }
    }
}
