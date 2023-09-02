@file:Suppress("MemberVisibilityCanBePrivate")

package com.etelie.application

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

        val current: ExecutionEnvironment
            get() = env?.let { fromLabel(it) } ?: UNKNOWN

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

fun ExecutionEnvironment?.isDeployable() = this?.isDeployable() ?: false
