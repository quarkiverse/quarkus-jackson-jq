package io.quarkiverse.quarkus.jackson.jq.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class JacksonJqProcessor {

    private static final String FEATURE = "jackson-jq";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
