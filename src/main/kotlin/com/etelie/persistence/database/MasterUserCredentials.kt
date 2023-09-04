package com.etelie.persistence.database

import kotlinx.serialization.Serializable

@Serializable
data class MasterUserCredentials(
    val username: String,
    val password: String,
)
