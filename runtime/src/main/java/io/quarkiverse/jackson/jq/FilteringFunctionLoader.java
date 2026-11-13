package io.quarkiverse.jackson.jq;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.thisptr.jackson.jq.v2.core.function.FunctionLoader;
import net.thisptr.jackson.jq.v2.spi.Function;
import net.thisptr.jackson.jq.v2.spi.FunctionSignature;
import net.thisptr.jackson.jq.v2.spi.JqFunction;
import net.thisptr.jackson.jq.v2.spi.version.Version;

/**
 * A {@link FunctionLoader} that decorates another one and only exposes the functions accepted by a
 * {@link FunctionFilter}.
 * <p>
 * The {@link FunctionContext#getLocator() locator} given to the filter is the class name of the decorated loader.
 * <p>
 * This loader does not cache its results. When it is added to an
 * {@link net.thisptr.jackson.jq.v2.core.EnvironmentBuilder}, the builder memoizes the results per jq {@link Version}.
 */
public class FilteringFunctionLoader implements FunctionLoader {
    private final FunctionLoader delegate;
    private final FunctionFilter filter;

    /**
     * @param delegate the loader that provides the functions to filter.
     * @param filter the filter that decides which functions are exposed.
     */
    public FilteringFunctionLoader(FunctionLoader delegate, FunctionFilter filter) {
        this.delegate = delegate;
        this.filter = filter;
    }

    @Override
    public Map<FunctionSignature, Function> getFunctions(Version jqVersion) {
        return filter(delegate.getFunctions(jqVersion));
    }

    @Override
    public Map<FunctionSignature, JqFunction> getJqFunctions(Version jqVersion) {
        return filter(delegate.getJqFunctions(jqVersion));
    }

    private <T> Map<FunctionSignature, T> filter(Map<FunctionSignature, T> functions) {
        Map<FunctionSignature, T> accepted = new LinkedHashMap<>();
        for (Map.Entry<FunctionSignature, T> entry : functions.entrySet()) {
            if (filter.accept(FunctionContext.of(entry.getKey(), delegate.getClass().getName()))) {
                accepted.put(entry.getKey(), entry.getValue());
            }
        }
        return Collections.unmodifiableMap(accepted);
    }
}
