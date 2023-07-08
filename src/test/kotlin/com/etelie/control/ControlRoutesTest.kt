package com.etelie.control

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ControlRoutesTest {

    @Test
    fun healthcheck() = testApplication {
        client.get("/control/healthcheck").also { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("Service is healthy", response.body())
        }
    }

}
