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

    fun isDeployable() = this == PRODUCTION || this == STAGING

    fun isUnknown(): Boolean = current == UNKNOWN
    fun isDevelopment(): Boolean = current == DEVELOPMENT
    fun isTest(): Boolean = current == TEST
    fun isStaging(): Boolean = current == STAGING
    fun isProduction(): Boolean = current == PRODUCTION
}

fun ExecutionEnvironment?.isDeployable() = this?.isDeployable() ?: false
