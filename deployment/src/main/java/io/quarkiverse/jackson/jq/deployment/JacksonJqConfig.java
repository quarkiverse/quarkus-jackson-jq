package io.quarkiverse.jackson.jq.deployment;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import net.thisptr.jackson.jq.v2.spi.version.Version;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
@ConfigMapping(prefix = "quarkus.jackson.jq")
public interface JacksonJqConfig {

    /**
     * Configure function related config
     */
    FunctionsConfig functions();

    @ConfigGroup
    interface FunctionsConfig {
        /**
         * The supported jq version
         */
        @WithDefault("1.8.2")
        @WithConverter(JacksonJqVersionConverter.class)
        Version version();

        /**
         * Comma-separated list of function names to exclude from the jq environment.
         * <p>
         * Functions are excluded by name, regardless of their arity, so excluding {@code min_by} removes every
         * {@code min_by} variant.
         * <p>
         * A query calling an excluded function fails to compile. Other jq functions defined in terms of an excluded
         * one can also stop working.
         */
        Optional<List<String>> excludes();
    }
}
