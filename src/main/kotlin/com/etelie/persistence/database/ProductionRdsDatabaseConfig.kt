package com.etelie.persistence.database

import com.etelie.application.ExecutionEnvironment
import io.ktor.server.application.ApplicationEnvironment

class ProductionRdsDatabaseConfig(
    applicationEnvironment: ApplicationEnvironment,
) : RdsDatabaseConfig(
    executionEnvironment = ExecutionEnvironment.PRODUCTION,
    applicationEnvironment = applicationEnvironment,
)
