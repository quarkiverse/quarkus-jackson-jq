package io.quarkiverse.jackson.jq.deployment;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.WithConverter;
import net.thisptr.jackson.jq.Version;

@ConfigRoot(name = JacksonJqConfig.CONFIG_PREFIX, phase = ConfigPhase.BUILD_TIME)
public class JacksonJqConfig {
    static final String CONFIG_PREFIX = "jackson.jq";

    /**
     * Configure function related config
     */
    @ConfigItem
    public FunctionsConfig functions;

    @ConfigGroup
    public static class FunctionsConfig {
        /**
         * The supported jq version
         */
        @ConfigItem(defaultValue = "1.6")
        @WithConverter(JacksonJqVersionConverter.class)
        public Version version;

        /**
         * List of functions to exclude
         */
        @ConfigItem
        public Optional<List<String>> excludes;
    }
}
