package io.quarkiverse.jackson.jq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.thisptr.jackson.jq.v2.core.module.ModuleLoader;
import net.thisptr.jackson.jq.v2.json.Maybe;
import net.thisptr.jackson.jq.v2.spi.annotations.ModuleRegistration;
import net.thisptr.jackson.jq.v2.spi.exception.JsonQueryException;
import net.thisptr.jackson.jq.v2.spi.module.JavaModule;
import net.thisptr.jackson.jq.v2.spi.module.Module;

/**
 * A {@link ModuleLoader} that decorates another one, typically one that resolves modules by the path of their
 * {@link ModuleRegistration} annotations, and only exposes the functions of the {@link JavaModule}s accepted by a
 * {@link FunctionFilter}.
 * <p>
 * The {@link FunctionContext#getLocator() locator} given to the filter is the import path of the module.
 * <p>
 * Only the {@link JavaModule}s are filtered. Any other module, e.g. a module written in jq source code, is returned
 * as it is. The modules are resolved once per path and then cached.
 *
 * @param <JsonNode> the JSON node type.
 */
public class FilteringModuleLoader<JsonNode> implements ModuleLoader<JsonNode> {

    private final ModuleLoader<JsonNode> delegate;
    private final FunctionFilter filter;
    private final Map<String, Module> moduleByPath = new ConcurrentHashMap<>();

    /**
     * @param delegate the loader that resolves the modules to filter.
     * @param filter the filter that decides which functions of the Java modules are exposed.
     */
    public FilteringModuleLoader(ModuleLoader<JsonNode> delegate, FunctionFilter filter) {
        this.delegate = delegate;
        this.filter = filter;
    }

    @Override
    public Module loadModule(String path, Maybe<JsonNode> metadata) throws JsonQueryException {
        Module module = moduleByPath.get(path);
        if (module != null) {
            return module;
        }
        module = delegate.loadModule(path, metadata);
        if (module instanceof JavaModule) {
            module = new FilteringJavaModule((JavaModule) module, filter, path);
        }
        moduleByPath.put(path, module);
        return module;
    }

    @Override
    public JsonNode loadData(String path, Maybe<JsonNode> metadata) throws JsonQueryException {
        return delegate.loadData(path, metadata);
    }
}
