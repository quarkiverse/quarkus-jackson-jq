package io.quarkiverse.jackson.jq.deployment;

import java.util.Objects;

import io.quarkiverse.jackson.jq.ModuleDescriptor;
import io.quarkus.builder.item.MultiBuildItem;

/**
 * A jackson-jq extension module that is active in the application, and the strategy to incorporate it into the
 * jq {@link net.thisptr.jackson.jq.v2.core.Environment}.
 * <p>
 * Use the {@code forRegister}, {@code forImport} and {@code forInclude} factory methods to create instances.
 */
public final class JacksonJqModuleBuildItem extends MultiBuildItem {
    private final ModuleDescriptor moduleDescriptor;
    private final JacksonJqConfig.Strategy strategy;
    private final String path;
    private final String alias;

    private JacksonJqModuleBuildItem(ModuleDescriptor moduleDescriptor,
            JacksonJqConfig.Strategy strategy, String path, String alias) {
        this.moduleDescriptor = moduleDescriptor;
        this.strategy = strategy;
        this.path = path;
        this.alias = alias;
    }

    /**
     * @param moduleDescriptor the module to register.
     * @param path the path to register the module under, {@code null} to use the module default path.
     * @return a build item with the {@code REGISTER} strategy.
     */
    public static JacksonJqModuleBuildItem forRegister(ModuleDescriptor moduleDescriptor, String path) {
        return new JacksonJqModuleBuildItem(moduleDescriptor, JacksonJqConfig.Strategy.REGISTER, path, null);
    }

    /**
     * @param moduleDescriptor the module to import.
     * @param alias the alias to import the module with, mandatory.
     * @return a build item with the {@code IMPORT} strategy.
     */
    public static JacksonJqModuleBuildItem forImport(ModuleDescriptor moduleDescriptor, String alias) {
        return new JacksonJqModuleBuildItem(moduleDescriptor, JacksonJqConfig.Strategy.IMPORT, null,
                Objects.requireNonNull(alias, "An alias must be provided."));
    }

    /**
     * @param moduleDescriptor the module to include.
     * @return a build item with the {@code INCLUDE} strategy.
     */
    public static JacksonJqModuleBuildItem forInclude(ModuleDescriptor moduleDescriptor) {
        return new JacksonJqModuleBuildItem(moduleDescriptor, JacksonJqConfig.Strategy.INCLUDE, null, null);
    }

    /**
     * @return the descriptor of the module.
     */
    public ModuleDescriptor getModuleDescriptor() {
        return moduleDescriptor;
    }

    /**
     * @return the strategy to incorporate the module into the environment.
     */
    public JacksonJqConfig.Strategy getStrategy() {
        return strategy;
    }

    /**
     * @return the registration path, only for the {@code REGISTER} strategy. {@code null} means the module default path.
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the import alias, only for the {@code IMPORT} strategy.
     */
    public String getAlias() {
        return alias;
    }
}
