package io.quarkiverse.jackson.jq;

/**
 * A jackson-jq module that is enabled in the application, together with the way it must be incorporated into the
 * {@link net.thisptr.jackson.jq.v2.core.Environment}. Created at build time and consumed by
 * {@link JacksonJqEnvironmentCreator}.
 *
 * @param moduleDescriptor the descriptor of the module.
 * @param filteringModule the fully qualified name of the build-time generated class that wraps the module in order to
 *        apply the {@link FunctionFilter}.
 * @param strategy the name of the configured strategy: {@code REGISTER}, {@code IMPORT} or {@code INCLUDE}.
 * @param alias the alias used to import the module. Only meaningful for the {@code IMPORT} strategy, {@code null}
 *        otherwise.
 * @param path the path to register the module under. Only meaningful for the {@code REGISTER} strategy, {@code null}
 *        or empty means the module default path.
 */
public record EnabledModule(ModuleDescriptor moduleDescriptor, String filteringModule, String strategy, String alias,
        String path) {

    /**
     * @return true if the module must be registered by path, false in any other case.
     */
    boolean isRegister() {
        return "REGISTER".equals(strategy);
    }

    /**
     * @return true if the module must be imported with an alias, false in any other case.
     */
    boolean isImport() {
        return "IMPORT".equals(strategy);
    }

    /**
     * @return true if the module functions must be included by their bare name, false in any other case.
     */
    boolean isInclude() {
        return "INCLUDE".equals(strategy);
    }
}
