package com.etelie

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun root() = testApplication {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("etelie api", bodyAsText())
        }
    }

    @Test
    fun healthcheck() = testApplication {
        client.get("/control/healthcheck").also { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("Service is healthy", response.body())
        }
    }

    @Test
    fun status() = testApplication {
        client.get("/control/status").also { response ->
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

}
