package io.quarkiverse.jackson.jq.deployment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import net.thisptr.jackson.jq.v2.spi.version.Version;

/**
 * Build time configuration of the jackson-jq extension.
 */
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
@ConfigMapping(prefix = "quarkus.jackson.jq")
public interface JacksonJqConfig {

    /**
     * The ways a module can be incorporated into the jq {@link net.thisptr.jackson.jq.v2.core.Environment}.
     */
    enum Strategy {
        /** The expression must {@code import} or {@code include} the module by its path. */
        REGISTER,
        /** The module functions are available as {@code alias::function}, without an {@code import} in the expression. */
        IMPORT,
        /** The module functions are available by their bare name, without an {@code include} in the expression. */
        INCLUDE
    }

    /** The strategy applied to a module when none is configured. */
    Strategy DEFAULT_STRATEGY = Strategy.REGISTER;

    /**
     * Configure function related configurations.
     */
    FunctionsConfig functions();

    /**
     * Configure module related configurations.
     */
    Map<String, JacksonJqModuleConfig> modules();

    @ConfigGroup
    interface FunctionsConfig {
        /**
         * The supported jq version.
         */
        @WithDefault("1.8.2")
        @WithConverter(JacksonJqVersionConverter.class)
        Version version();

        /**
         * List of functions to exclude.
         */
        Optional<List<String>> excludes();
    }

    @ConfigGroup
    interface JacksonJqModuleConfig {

        /**
         * Enables/disables a particular module. A jackson-jq module included as project dependency is enabled by default.
         *
         * @return true if the module is enabled, false in any other case.
         */
        @WithDefault("true")
        boolean enabled();

        /**
         * Configures strategy for incorporating the jackson-jq module into the Environment.
         *
         * @return the configured strategy.
         */
        @WithDefault("register")
        Strategy strategy();

        /**
         * Mandatory if strategy is IMPORT. Ignored otherwise.
         *
         * @return The alias used to import the module into the Environment.
         */
        Optional<String> alias();

        /**
         * Optional. Only applies if strategy is REGISTER.
         *
         * @return The path for the module registration into the Environment.
         */
        Optional<String> path();
    }
}
