package io.quarkiverse.jackson.jq;

import java.util.HashSet;
import java.util.List;

import net.thisptr.jackson.jq.v2.core.Environment;
import net.thisptr.jackson.jq.v2.core.EnvironmentBuilder;
import net.thisptr.jackson.jq.v2.core.function.loaders.ClassPathFunctionLoader;
import net.thisptr.jackson.jq.v2.core.module.loaders.ClassPathModuleLoader;
import net.thisptr.jackson.jq.v2.json.impl.jackson3.Jackson3JsonProvider;
import net.thisptr.jackson.jq.v2.spi.version.Version;
import tools.jackson.databind.JsonNode;

/**
 * Creates the jackson-jq {@link Environment} exposed by the extension.
 */
public class JacksonJqEnvironmentCreator {

    private JacksonJqEnvironmentCreator() {
    }

    /**
     * Creates an {@link Environment} that finds the functions and the modules in the classpath of the current thread,
     * excluding the functions with the given names. See {@link FunctionFilter#denyNames(java.util.Set)}.
     *
     * @param version the jq version, e.g. {@code 1.8.2}.
     * @param excludes the names of the functions to exclude, regardless of their arity. May be empty.
     * @return the new environment.
     */
    public static Environment<JsonNode> createEnvironment(String version, List<String> excludes) {
        FunctionFilter filter = FunctionFilter.denyNames(new HashSet<>(excludes));
        return EnvironmentBuilder
                .withDefaultLoaders(Jackson3JsonProvider.getInstance(), Version.valueOf(version))
                .clearModuleLoaders()
                .clearFunctionLoaders()
                .addFunctionLoader(new FilteringFunctionLoader(
                        new ClassPathFunctionLoader(Thread.currentThread().getContextClassLoader()), filter))
                .addModuleLoader(new FilteringModuleLoader<>(
                        new ClassPathModuleLoader<>(Thread.currentThread().getContextClassLoader()), filter))
                .build();
    }
}
