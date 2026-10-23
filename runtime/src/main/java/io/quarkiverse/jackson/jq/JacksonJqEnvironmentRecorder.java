package io.quarkiverse.jackson.jq;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import net.thisptr.jackson.jq.v2.core.Environment;
import net.thisptr.jackson.jq.v2.core.EnvironmentBuilder;
import net.thisptr.jackson.jq.v2.json.impl.jackson3.Jackson3JsonProvider;
import net.thisptr.jackson.jq.v2.spi.version.Version;
import tools.jackson.databind.JsonNode;

@Recorder
public class JacksonJqEnvironmentRecorder {

    public RuntimeValue<Environment<JsonNode>> createEnvironment(String version) {
        Environment<JsonNode> environment = EnvironmentBuilder
                .withDefaultLoaders(Jackson3JsonProvider.getInstance(), Version.valueOf(version),
                        Thread.currentThread().getContextClassLoader())
                .build();
        return new RuntimeValue<>(environment);
    }
}
