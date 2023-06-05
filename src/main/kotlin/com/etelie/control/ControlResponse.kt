package com.etelie.control

import kotlinx.serialization.Serializable

@Serializable
data class ControlResponse(
    val identifier: String,
    val state: Int,
) {
    companion object {
        suspend fun fromControl(control: Control) = ControlResponse(
            control.identifier,
            control.fetchEntity()?.state ?: -1,
        )
    }
}
