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
        private val env: String? = System.getenv("EXECUTION_ENVIRONMENT")

        val current: ExecutionEnvironment
            get() = env?.let { fromLabel(it) } ?: UNKNOWN

        private fun fromLabel(label: String) = values().firstOrNull {
            it.label.equals(label, ignoreCase = true)
        }
    }

    fun deployable() = this == PRODUCTION || this == STAGING
}

fun ExecutionEnvironment?.deployable() = this?.deployable() ?: false
