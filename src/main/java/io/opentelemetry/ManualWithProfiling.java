package io.opentelemetry;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;

import java.util.concurrent.TimeUnit;

public class ManualWithProfiling {

    private final Tracer tracer;

    public ManualWithProfiling(Tracer tracer) {
        this.tracer = tracer;
    }

    public static void main(String[] args) throws Exception {
        main3(args);
    }

    public static void main1(String[] args) throws Exception {
        OtlpGrpcSpanExporter exporter = OtlpGrpcSpanExporter.builder().build();

        try (SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .setResource(Resource.create(Attributes.of(AttributeKey.stringKey("service.name"), "manual_with_profiling",
                        AttributeKey.stringKey("service.instance.id"), "abcd1234",
                        AttributeKey.stringKey("deployment.environment"), "jjp-dev")))
                .addSpanProcessor(BatchSpanProcessor.builder(exporter).build())
                .build()) {

            Tracer tracer = sdkTracerProvider.get("jp-test2");
            ManualWithProfiling app = new ManualWithProfiling(tracer);
            app.runForever();
        }
    }

    public static void main3(String[] args) throws Exception {
        OtlpGrpcSpanExporter exporter = OtlpGrpcSpanExporter.builder().build();

        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .setResource(Resource.create(Attributes.of(AttributeKey.stringKey("service.name"), "manual_with_profiling",
                        AttributeKey.stringKey("service.instance.id"), "abcd1234",
                        AttributeKey.stringKey("deployment.environment"), "jjp-dev")))
                .addSpanProcessor(BatchSpanProcessor.builder(exporter).build())
                .build();

        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .buildAndRegisterGlobal();

        Tracer tracer = sdkTracerProvider.get("jp-test2");
        ManualWithProfiling app = new ManualWithProfiling(tracer);
        app.runForever();
    }

    public static void main2(String[] args) throws Exception {
        TimeUnit.SECONDS.sleep(15);
        OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
        Tracer tracer = openTelemetry.getTracer("jp-test2");
        ManualWithProfiling app = new ManualWithProfiling(tracer);
        app.runForever();
    }

    private void runForever() throws Exception {
        while(true){
            doSpanAction();
        }
    }

    private void doSpanAction() throws Exception {
        Span span = tracer.spanBuilder("test-span2")
                .setSpanKind(SpanKind.SERVER)
                .setAttribute("test", "foobar")
                .startSpan();
        System.out.println("Created a new span!");
        try (Scope scope = span.makeCurrent()) {
            // fake workload here
            TimeUnit.SECONDS.sleep(5);
        }
        span.end();
        System.out.println("   (span complete)");
    }
}
