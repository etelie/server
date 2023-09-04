@file:Suppress("MemberVisibilityCanBePrivate")

package com.etelie.application

import io.ktor.server.application.ApplicationEnvironment

private val log = logger { }

enum class ExecutionEnvironment(
    val label: String,
) {

    UNKNOWN("unknown"),
    DEVELOPMENT("development"),
    TEST("test"),
    STAGING("staging"),
    PRODUCTION("production");

    companion object {
        private val env: String? by lazy { System.getenv("EXECUTION_ENVIRONMENT") }
        private var initialized = false
        private var _applicationEnvironment: ApplicationEnvironment? = null

        val current: ExecutionEnvironment
            get() {
                if (!initialized) {
                    log.warn { "Evaluating execution environment before initialization" }
                }
                return env?.let { fromLabel(it) }
                    ?: if (applicationEnvironment.developmentMode) DEVELOPMENT else UNKNOWN
            }
        val applicationEnvironment: ApplicationEnvironment
            get() {
                check(initialized) { "Cannot access application environment if ExecutionEnvironment has not been initialized" }
                return _applicationEnvironment!!
            }

        fun initialize(applicationEnvironment: ApplicationEnvironment) {
            _applicationEnvironment = applicationEnvironment
            initialized = true
        }

        private fun fromLabel(label: String) = values().firstOrNull {
            it.label.equals(label, ignoreCase = true)
        }
    }

    fun isDeployable() = isProduction() || isStaging()
    fun isServer() = isDeployable() || isDevelopment()

    fun isUnknown(): Boolean = this == UNKNOWN
    fun isDevelopment(): Boolean = this == DEVELOPMENT
    fun isTest(): Boolean = this == TEST
    fun isStaging(): Boolean = this == STAGING
    fun isProduction(): Boolean = this == PRODUCTION

}
