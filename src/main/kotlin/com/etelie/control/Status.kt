package com.etelie.control

enum class Status(val state: Int) {
    OPERATIONAL(0),
    STOPPED(1),
    MAINTENANCE(2),
    DEVELOPMENT(3),
    UNKNOWN(4);

    companion object {
        private fun fromState(state: Int) = values().firstOrNull {
            it.state == state
        }

        suspend fun fetchCurrent() = Control.STATUS.fetchEntity()?.state?.let {
            fromState(it)
        }
    }
}
