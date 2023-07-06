package com.etelie.monitoring

import com.etelie.application.ExecutionEnvironment
import com.etelie.application.isDeployable
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator
import io.opentelemetry.context.propagation.ContextPropagators
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.logs.SdkLoggerProvider
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor
import io.opentelemetry.sdk.metrics.SdkMeterProvider
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes

fun getOpenTelemetry(executionEnvironment: ExecutionEnvironment?): OpenTelemetry {
    if (!executionEnvironment.isDeployable()) {
        return OpenTelemetry.noop()
    }

    val resource: Resource = Resource.getDefault()
        .merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, "server-${executionEnvironment!!.label}")))

    val tracerProvider: SdkTracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(BatchSpanProcessor.builder(OtlpHttpSpanExporter.getDefault()).build())
        .setResource(resource)
        .build()

    val meterProvider: SdkMeterProvider = SdkMeterProvider.builder()
        .registerMetricReader(PeriodicMetricReader.builder(OtlpHttpMetricExporter.getDefault()).build())
        .setResource(resource)
        .build()

    val loggerProvider: SdkLoggerProvider = SdkLoggerProvider.builder()
        .addLogRecordProcessor(BatchLogRecordProcessor.builder(OtlpHttpLogRecordExporter.getDefault()).build())
        .setResource(resource)
        .build()

    val contextPropagators: ContextPropagators = ContextPropagators.create(W3CTraceContextPropagator.getInstance())

    return OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .setMeterProvider(meterProvider)
        .setLoggerProvider(loggerProvider)
        .setPropagators(contextPropagators)
        .buildAndRegisterGlobal()
}
