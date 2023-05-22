package com.etelie

import com.etelie.plugin.pluginApi
import com.etelie.plugin.pluginHTTP
import com.etelie.plugin.pluginMonitoring
import com.etelie.plugin.pluginRouting
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    private fun Application.setup() {
        pluginApi()
        pluginHTTP()
        pluginMonitoring()
        pluginRouting()
    }

    @Test
    fun root() = testApplication {
        application { setup() }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("etelie api", bodyAsText())
        }
    }

    @Test
    fun healthcheck() = testApplication {
        application { setup() }
        client.get("/healthcheck").also { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("Service is healthy", response.body())
        }
    }

}
