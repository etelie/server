package com.etelie.persistence.database

import aws.sdk.kotlin.services.rds.RdsClient
import aws.sdk.kotlin.services.rds.model.DbInstance
import aws.sdk.kotlin.services.rds.model.DescribeDbInstancesRequest
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.etelie.application.ExecutionEnvironment
import com.etelie.application.logger
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val log = logger { }

sealed class RdsDatabaseConfig(
    executionEnvironment: ExecutionEnvironment,
    val applicationEnvironment: ApplicationEnvironment,
) : DatabaseConfig(
    executionEnvironment = executionEnvironment,
) {

    init {
        require(executionEnvironment.isDeployable())
    }

    private val instanceName: String
        get() = applicationEnvironment.config
            .property("etelie.aws.rds.db_instance_identifier")
            .getString()
    private val instance: DbInstance by lazy {
        runBlocking {
            getRdsInstance(instanceName)!!
        }
    }

    override val host: String by lazy {
        instance.endpoint!!.address!!
    }
    override val port: Int by lazy {
        instance.endpoint!!.port.toInt()
    }
    override val user: String by lazy {
        instance.masterUsername!!
    }
    override val password: String by lazy {
        runBlocking {
            getRdsPassword(instance.masterUserSecret!!.secretArn!!)!!
        }
    }
    override val database: String by lazy {
        instance.dbName!!
    }
    override val maxConnections: Int
        get() = applicationEnvironment.config
            .property("etelie.hikaricp.max_connections")
            .getString()
            .toInt()

    private suspend fun getRdsInstance(id: String): DbInstance? {
        log.info { "Reading configuration of RDS instance [$instanceName]" }

        val rdsClient = RdsClient.fromEnvironment()
        val request = DescribeDbInstancesRequest {
            dbInstanceIdentifier = id
        }
        val response = rdsClient.describeDbInstances(request)

        return response.dbInstances?.getOrNull(0)
    }

    private suspend fun getRdsPassword(secretArn: String): String? {
        log.info { "Reading secret value from [${secretArn}] with status [${instance.masterUserSecret?.secretStatus}]" }

        val secretsManagerClient = SecretsManagerClient.fromEnvironment()
        val request = GetSecretValueRequest {
            secretId = secretArn
        }
        val response = secretsManagerClient.getSecretValue(request)

        return response.secretString?.let { secret ->
            Json.decodeFromString<MasterUserCredentials>(secret).password
        }
    }

}
