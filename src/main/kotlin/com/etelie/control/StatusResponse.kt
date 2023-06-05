package com.etelie.control

import kotlinx.serialization.Serializable

@Serializable
data class StatusResponse(
    val control: ControlResponse,
    val text: String,
)
