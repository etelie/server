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
    fun isServer() = isDeployable() || this == DEVELOPMENT

    fun isUnknown(): Boolean = current == UNKNOWN
    fun isDevelopment(): Boolean = current == DEVELOPMENT
    fun isTest(): Boolean = current == TEST
    fun isStaging(): Boolean = current == STAGING
    fun isProduction(): Boolean = current == PRODUCTION

    fun getHosts(): Collection<String> = if (isProduction()) {
        setOf("etelie.com")
    } else if (isStaging()) {
        setOf("qa.etelie.com")
        // storybook.qa.etelie.com is intentionally excluded to forbid API requests via CORS
    } else {
        setOf("localhost", "127.0.0.1", "192.168.0.1", "0.0.0.0", "::1").flatMap {
            setOf(it, "$it:3000")
        }
    }
}

fun ExecutionEnvironment?.isDeployable() = this?.isDeployable() ?: false
