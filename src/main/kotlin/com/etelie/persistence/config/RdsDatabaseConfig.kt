package com.etelie.persistence.config

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.amazonaws.regions.Regions
import com.amazonaws.services.rds.AmazonRDSClient
import com.amazonaws.services.rds.model.DBInstance
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest
import com.etelie.application.ExecutionEnvironment
import com.etelie.application.logger
import com.etelie.persistence.MasterUserCredentials
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val log = logger { }

sealed class RdsDatabaseConfig(
    final override val executionEnvironment: ExecutionEnvironment,
    protected open val applicationEnvironment: ApplicationEnvironment,
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
    private val instance: DBInstance by lazy {
        getRdsInstance(instanceName)!!
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

    private fun getRdsInstance(id: String): DBInstance? {
        log.info { "Reading configuration of RDS instance [$instanceName]" }

        // todo: revert to kotlin SDK
        val rdsClient = AmazonRDSClient.builder()
            .withRegion(Regions.US_EAST_1)
            .build()
        val response = rdsClient.describeDBInstances(
            DescribeDBInstancesRequest()
                .withDBInstanceIdentifier(id),
        )
        val instance = response.dbInstances.getOrNull(0)

        check(instance != null)
        check(instance.dbInstanceIdentifier == instanceName)
        return instance
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
