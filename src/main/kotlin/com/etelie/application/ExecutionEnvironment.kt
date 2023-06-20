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
        val current: ExecutionEnvironment
            get() = env?.let { fromLabel(it) } ?: UNKNOWN

        private val env: String? by lazy {
            System.getenv("EXECUTION_ENVIRONMENT")
        }

        private fun fromLabel(label: String) = values().firstOrNull {
            it.label.equals(label, ignoreCase = true)
        }
    }

    fun deployable() = this == PRODUCTION || this == STAGING
}

fun ExecutionEnvironment?.deployable() = this?.deployable() ?: false
