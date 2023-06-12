package com.etelie.control.status

import com.etelie.control.ControlResponse
import kotlinx.serialization.Serializable

@Serializable
data class StatusResponse(
    val control: ControlResponse,
    val text: String,
)
