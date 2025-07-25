package com.etelie.persistence.database

import com.etelie.application.ExecutionEnvironment
import io.ktor.server.application.ApplicationEnvironment

class StagingRdsDatabaseConfig(
    applicationEnvironment: ApplicationEnvironment,
) : RdsDatabaseConfig(
    executionEnvironment = ExecutionEnvironment.STAGING,
    applicationEnvironment = applicationEnvironment,
)
