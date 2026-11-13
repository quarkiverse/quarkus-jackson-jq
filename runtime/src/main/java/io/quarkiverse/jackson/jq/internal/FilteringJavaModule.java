package io.quarkiverse.jackson.jq.internal;

import java.util.HashMap;
import java.util.Map;

import net.thisptr.jackson.jq.v2.spi.Function;
import net.thisptr.jackson.jq.v2.spi.FunctionSignature;
import net.thisptr.jackson.jq.v2.spi.module.JavaModule;
import net.thisptr.jackson.jq.v2.spi.module.ModuleMeta;

/**
 * A {@link JavaModule} that decorates another one and only exposes the functions accepted by a {@link FunctionFilter}.
 * <p>
 * The filter is applied once, when the module is created, and the resulting functions are an immutable snapshot.
 */
public class FilteringJavaModule implements JavaModule {
    private final JavaModule delegate;
    private final Map<FunctionSignature, Function> functions;

    /**
     * @param module the module to decorate.
     * @param filter the filter that decides which functions of the module are exposed.
     * @param locator the {@link FunctionContext#getLocator() locator} given to the filter, the import path of the
     *        module.
     */
    protected FilteringJavaModule(JavaModule module, FunctionFilter filter, String locator) {
        this.delegate = module;
        HashMap<FunctionSignature, Function> filteredFunctions = new HashMap<>();
        for (Map.Entry<FunctionSignature, Function> entry : module.getFunctions().entrySet()) {
            if (filter.accept(FunctionContext.of(entry.getKey(), locator))) {
                filteredFunctions.put(entry.getKey(), entry.getValue());
            }
        }
        this.functions = Map.copyOf(filteredFunctions);
    }

    @Override
    public Map<FunctionSignature, Function> getFunctions() {
        return functions;
    }

    @Override
    public ModuleMeta getModuleMeta() {
        return delegate.getModuleMeta();
    }
}
