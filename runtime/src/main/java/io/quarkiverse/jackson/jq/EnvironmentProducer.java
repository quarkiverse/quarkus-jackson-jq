package io.quarkiverse.jackson.jq;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import net.thisptr.jackson.jq.v2.core.Environment;
import tools.jackson.databind.JsonNode;

/**
 * Exposes the jackson-jq {@link Environment} built by the extension as a CDI bean, so it can be injected with
 * {@code @Inject Environment<JsonNode> environment;}.
 */
@Singleton
public class EnvironmentProducer {

    private final EnvironmentSupplier environmentSupplier;

    public EnvironmentProducer(EnvironmentSupplier environmentSupplier) {
        this.environmentSupplier = environmentSupplier;
    }

    @Produces
    @Singleton
    public Environment<JsonNode> produce() {
        return environmentSupplier.get();
    }
}
