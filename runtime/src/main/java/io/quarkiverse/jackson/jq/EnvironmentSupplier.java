package io.quarkiverse.jackson.jq;

import java.util.function.Supplier;

import net.thisptr.jackson.jq.v2.core.Environment;
import tools.jackson.databind.JsonNode;

/**
 * Holds the {@link Environment} created at static init time. It is registered as a synthetic bean by the extension
 * and consumed by {@link EnvironmentProducer}.
 */
public class EnvironmentSupplier implements Supplier<Environment<JsonNode>> {

    Environment<JsonNode> environment;

    /**
     * @param environment the already built {@link Environment}.
     */
    public EnvironmentSupplier(Environment<JsonNode> environment) {
        this.environment = environment;
    }

    @Override
    public Environment<JsonNode> get() {
        return environment;
    }
}
