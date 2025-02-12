package io.quarkiverse.jackson.jq.deployment;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import net.thisptr.jackson.jq.Version;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
@ConfigMapping(prefix = "quarkus.jackson.jq")
public interface JacksonJqConfig {

    /**
     * Configure function related config
     */
    FunctionsConfig functions();

    @ConfigGroup
    public interface FunctionsConfig {
        /**
         * The supported jq version
         */
        @WithDefault("1.6")
        @WithConverter(JacksonJqVersionConverter.class)
        Version version();

        /**
         * List of functions to exclude
         */
        Optional<List<String>> excludes();
    }
}
