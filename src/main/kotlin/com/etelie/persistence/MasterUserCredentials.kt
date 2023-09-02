package com.etelie.persistence

import kotlinx.serialization.Serializable

@Serializable
data class MasterUserCredentials(
    val username: String,
    val password: String,
)
