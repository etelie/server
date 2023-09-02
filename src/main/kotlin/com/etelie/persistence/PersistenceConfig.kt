package com.etelie.persistence

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.amazonaws.regions.Regions
import com.amazonaws.services.rds.AmazonRDSClient
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest
import com.etelie.application.ExecutionEnvironment
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

private val log = KotlinLogging.logger {}

object PersistenceConfig {

    /**
     * The fully qualified dot-separated name of the class, or null if the class is local or a class of an anonymous object.
     * @see kotlin.reflect.KClass.qualifiedName
     */
    private val driverClassName: String = org.postgresql.Driver::class.qualifiedName!!

    fun connectToDatabase(environment: ApplicationEnvironment) = runBlocking {
        val dataSource: HikariDataSource =
            if (ExecutionEnvironment.current.isDeployable()) {
                createRdsDataSource(environment)
            } else {
                createLocalDataSource(environment)
            }

        log.info("Attempting to connect Exposed to ${dataSource.jdbcUrl}")
        val database = Database.connect(dataSource)
        TransactionManager.defaultDatabase = database
        log.info("Successfully connected to ${database.url}")
    }

    private fun createLocalDataSource(environment: ApplicationEnvironment): HikariDataSource {
        val host = environment.config.property("etelie.postgresql.deploy.host").getString()
        val port = environment.config.property("etelie.postgresql.deploy.port").getString()
        val user = environment.config.property("etelie.postgresql.credential.user").getString()
        val password = environment.config.property("etelie.postgresql.credential.password").getString()
        val db = environment.config.property("etelie.postgresql.database").getString()
        val maxConnections = environment.config.property("hikaricp.max_connections").getString().toInt()

        val jdbcUrl = getJdbcUrl(host, port, db)
        return createHikariDataSource(jdbcUrl, user, password, maxConnections)
    }

    private suspend fun createRdsDataSource(environment: ApplicationEnvironment): HikariDataSource {
        val instanceName = environment.config.property("etelie.aws.rds.db_instance_identifier").getString()
        log.info { "Reading configuration of RDS instance [$instanceName]" }

        val instance = getRdsInstance(instanceName)!!
        check(instance.dbInstanceIdentifier == instanceName)

        val secretArn = instance.masterUserSecret!!.secretArn!!
        log.info { "Reading secret value from [${secretArn}] with status [${instance.masterUserSecret?.secretStatus}]" }

        val password = getRdsPassword(secretArn)!!

        val host: String = instance.endpoint!!.address!!
        val port: String = instance.endpoint!!.port.toString()
        val db: String = instance.dbName!!
        val user: String = instance.masterUsername!!
        val maxConnections = environment.config.property("hikaricp.max_connections").getString().toInt()

        val jdbcUrl = getJdbcUrl(host, port, db)
        return createHikariDataSource(jdbcUrl, user, password, maxConnections)
    }

    private fun getRdsInstance(id: String): com.amazonaws.services.rds.model.DBInstance? {
        val rdsClient = AmazonRDSClient.builder()
            .withRegion(Regions.US_EAST_1)
            .build()
        val response = rdsClient.describeDBInstances(
            DescribeDBInstancesRequest()
                .withDBInstanceIdentifier(id),
        )
        return response.dbInstances.getOrNull(0)
    }

    private suspend fun getRdsPassword(secretArn: String): String? {
        val secretsManagerClient = SecretsManagerClient.fromEnvironment()
        val request = GetSecretValueRequest {
            secretId = secretArn
        }
        val response = secretsManagerClient.getSecretValue(request)
        return response.secretString?.let { secret ->
            Json.decodeFromString<MasterUserCredentials>(secret).password
        }
    }

    private fun getJdbcUrl(host: String, port: String, db: String): String =
        "jdbc:postgresql://$host:$port/$db"

    private fun createHikariDataSource(
        jdbcUrl: String,
        user: String,
        password: String,
        maxConnections: Int,
    ): HikariDataSource {
        log.info("Attempting to connect HikariCP to $jdbcUrl")
        return HikariDataSource(
            HikariConfig().also {
                it.driverClassName = driverClassName
                it.jdbcUrl = jdbcUrl
                it.maximumPoolSize = maxConnections
                it.isAutoCommit = false
                it.transactionIsolation = Connection::TRANSACTION_REPEATABLE_READ.name
                it.username = user
                it.password = password
                it.validate()
            },
        )
    }

}
