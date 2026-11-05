package io.quarkiverse.jackson.jq;

import static io.quarkiverse.jackson.jq.Utils.createInstance;

import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thisptr.jackson.jq.v2.core.Environment;
import net.thisptr.jackson.jq.v2.core.EnvironmentBuilder;
import net.thisptr.jackson.jq.v2.core.function.loaders.BuiltinFunctionLoader;
import net.thisptr.jackson.jq.v2.json.impl.jackson3.Jackson3JsonProvider;
import net.thisptr.jackson.jq.v2.spi.module.Module;
import net.thisptr.jackson.jq.v2.spi.version.Version;
import tools.jackson.databind.JsonNode;

/**
 * Creates the jackson-jq {@link Environment} exposed by the extension.
 */
public class JacksonJqEnvironmentCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonJqEnvironmentCreator.class);

    private JacksonJqEnvironmentCreator() {
    }

    /**
     * Creates an {@link Environment} with the built-in functions, the custom functions and the enabled modules. The
     * excluded functions are removed from the built-in functions, the custom functions and the Java modules.
     *
     * @param version the jq version.
     * @param excludes the names of the functions to exclude.
     * @param enabledModules the modules to incorporate into the environment.
     * @param customFunctions the custom functions discovered at build time.
     * @return the environment.
     */
    public static Environment<JsonNode> createEnvironment(String version, List<String> excludes,
            List<EnabledModule> enabledModules, List<FunctionDescriptor> customFunctions) {
        FunctionFilter filter = FunctionFilter.denyNames(new HashSet<>(excludes));
        EnvironmentBuilder<JsonNode> builder = EnvironmentBuilder
                .withDefaultLoaders(Jackson3JsonProvider.getInstance(), Version.valueOf(version))
                .clearFunctionLoaders()
                .clearModuleLoaders()
                .addFunctionLoader(new FilteringFunctionLoader(BuiltinFunctionLoader.getInstance(), filter))
                .addFunctionLoader(
                        new FilteringFunctionLoader(new InMemoryFunctionLoader(customFunctions, Version.valueOf(version)),
                                filter));

        for (EnabledModule enabledModule : enabledModules) {
            LOGGER.debug("Registering jackson-jq module: {}:{} with strategy: {}, alias: {}, path: {}",
                    enabledModule.moduleDescriptor().groupId(), enabledModule.moduleDescriptor().artifactId(),
                    enabledModule.strategy(), enabledModule.alias(), enabledModule.path());
            Module filteringModule = instantiateModule(enabledModule.filteringModule(), filter,
                    enabledModule.moduleDescriptor().id());
            if (enabledModule.isImport()) {
                builder.importModule(filteringModule, enabledModule.alias());
            } else if (enabledModule.isRegister()) {
                if (enabledModule.path() == null || enabledModule.path().isEmpty()) {
                    builder.registerModule(filteringModule);
                } else {
                    builder.registerModule(enabledModule.path(), filteringModule);
                }
            } else {
                builder.includeModule(filteringModule);
            }
        }
        return builder.build();
    }

    private static Module instantiateModule(String className, FunctionFilter filter, String locator) {
        try {
            return createInstance(className, new Class<?>[] { FunctionFilter.class, String.class }, filter, locator);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(
                    "Failed to instantiate jackson-jq module: " + className, e);
        }
    }
}
