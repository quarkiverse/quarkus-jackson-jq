package io.quarkiverse.jackson.jq;

import static io.quarkiverse.jackson.jq.Utils.createInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.thisptr.jackson.jq.v2.core.function.FunctionLoader;
import net.thisptr.jackson.jq.v2.spi.Function;
import net.thisptr.jackson.jq.v2.spi.FunctionSignature;
import net.thisptr.jackson.jq.v2.spi.JqFunction;
import net.thisptr.jackson.jq.v2.spi.version.Version;
import net.thisptr.jackson.jq.v2.spi.version.VersionRange;

/**
 * A {@link FunctionLoader} that provides the custom functions discovered at build time. Only the functions whose
 * version range contains the configured jq version are instantiated.
 */
public class InMemoryFunctionLoader implements FunctionLoader {

    private final Map<FunctionSignature, Function> functions = new HashMap<>();

    /**
     * @param functionDescriptors the custom functions discovered at build time.
     * @param version the configured jq version.
     */
    public InMemoryFunctionLoader(List<FunctionDescriptor> functionDescriptors, Version version) {
        functionDescriptors.forEach(descriptor -> {
            VersionRange versionRange = VersionRange.valueOf(descriptor.versionRange());
            if (versionRange.contains(version)) {
                functions.put(FunctionSignature.of(descriptor.name(), descriptor.nargs()), instantiateFunction(descriptor));
            }
        });
    }

    @Override
    public Map<FunctionSignature, Function> getFunctions(Version jqVersion) {
        return functions;
    }

    @Override
    public Map<FunctionSignature, JqFunction> getJqFunctions(Version jqVersion) {
        return Map.of();
    }

    private static Function instantiateFunction(FunctionDescriptor descriptor) {
        try {
            return createInstance(descriptor.className());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(
                    "Failed to instantiate jackson-jq function: " + descriptor.className(), e);
        }
    }
}
