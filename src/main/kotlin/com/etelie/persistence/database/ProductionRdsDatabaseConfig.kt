package com.etelie.persistence.database

import com.etelie.application.ExecutionEnvironment
import io.ktor.server.application.ApplicationEnvironment

class ProductionRdsDatabaseConfig(
    override val applicationEnvironment: ApplicationEnvironment,
) : RdsDatabaseConfig(
    executionEnvironment = ExecutionEnvironment.PRODUCTION,
    applicationEnvironment = applicationEnvironment,
)
