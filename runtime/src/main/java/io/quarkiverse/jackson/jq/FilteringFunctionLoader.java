package io.quarkiverse.jackson.jq;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.thisptr.jackson.jq.v2.core.function.FunctionLoader;
import net.thisptr.jackson.jq.v2.spi.Function;
import net.thisptr.jackson.jq.v2.spi.FunctionSignature;
import net.thisptr.jackson.jq.v2.spi.JqFunction;
import net.thisptr.jackson.jq.v2.spi.version.Version;

/**
 * A {@link FunctionLoader} that decorates another one and only exposes the functions accepted by a
 * {@link FunctionFilter}. The filtered result is cached per jq {@link Version}.
 * <p>
 * The {@link FunctionContext#getLocator() locator} given to the filter is the class name of the decorated loader.
 */
public class FilteringFunctionLoader implements FunctionLoader {
    private final FunctionLoader delegate;
    private final FunctionFilter filter;
    private final ConcurrentHashMap<Version, Map<FunctionSignature, Function>> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Version, Map<FunctionSignature, JqFunction>> jqCache = new ConcurrentHashMap<>();

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
        return cache.computeIfAbsent(jqVersion, v -> filter(delegate.getFunctions(v)));
    }

    @Override
    public Map<FunctionSignature, JqFunction> getJqFunctions(Version jqVersion) {
        return jqCache.computeIfAbsent(jqVersion, v -> filter(delegate.getJqFunctions(v)));
    }

    private <T> Map<FunctionSignature, T> filter(Map<FunctionSignature, T> functions) {
        Map<FunctionSignature, T> accepted = new LinkedHashMap<>();
        for (Map.Entry<FunctionSignature, T> entry : functions.entrySet()) {
            if (filter
                    .accept(FunctionContext.of(entry.getKey(), delegate.getClass().getName())))
                accepted.put(entry.getKey(), entry.getValue());
        }
        return Collections.unmodifiableMap(accepted);
    }
}
