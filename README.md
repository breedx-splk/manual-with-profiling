
```
-javaagent:./splunk-otel-javaagent-1.13.0-SNAPSHOT.jar
-Dotel.instrumentation.common.default-enabled=false
-Dotel.javaagent.debug=true
-Dsplunk.profiler.enabled=true
-Dsplunk.profiler.memory.enabled=true
-Dsplunk.profiler.call.stack.interval=1000
-Dotel.service.name=manual_with_profiling
-Dotel.resource.attributes=deployment.environment=jjp-dev
-Dsplunk.profiler.keep-files=true

```