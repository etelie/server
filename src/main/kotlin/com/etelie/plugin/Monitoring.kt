package com.etelie.plugin

import com.etelie.executionEnvironment
import com.etelie.monitoring.getOpenTelemetry
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.path
import io.opentelemetry.instrumentation.ktor.v2_0.server.KtorServerTracing
import org.slf4j.event.Level

fun Application.pluginMonitoring() {

    install(KtorServerTracing) {
        setOpenTelemetry(getOpenTelemetry(executionEnvironment))
    }

    install(CallLogging) {
        level = Level.DEBUG
        filter { call -> call.request.path().startsWith("/") }
        callIdMdc("call-id")
    }

    install(CallId) {
        header(HttpHeaders.XRequestId)
        verify { callId: String ->
            callId.isNotEmpty()
        }
    }

}
