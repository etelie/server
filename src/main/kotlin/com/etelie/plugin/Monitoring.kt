package com.etelie.plugin

import com.etelie.ExecutionEnvironment
import com.etelie.monitoring.getOpenTelemetry
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.opentelemetry.instrumentation.ktor.v2_0.server.KtorServerTracing
import org.slf4j.event.Level

fun Application.pluginMonitoring() {

    install(KtorServerTracing) {
        setOpenTelemetry(getOpenTelemetry(ExecutionEnvironment.current))
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
