package com.etelie

enum class ExecutionEnvironment(
    val label: String,
) {
    UNKNOWN("unknown"),
    DEVELOPMENT("development"),
    TEST("test"),
    STAGING("staging"),
    PRODUCTION("production");

    companion object {
        fun fromLabel(label: String) = values().firstOrNull {
            it.label.equals(label, ignoreCase = true)
        }
    }

    fun deployable() = this == PRODUCTION || this == STAGING
}

fun ExecutionEnvironment?.deployable() = this?.deployable() ?: false
