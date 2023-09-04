package com.etelie.persistence.config

import com.etelie.application.ExecutionEnvironment
import io.ktor.server.application.ApplicationEnvironment

class StagingRdsDatabaseConfig(
    override val applicationEnvironment: ApplicationEnvironment,
) : RdsDatabaseConfig(
    executionEnvironment = ExecutionEnvironment.STAGING,
    applicationEnvironment = applicationEnvironment,
)
