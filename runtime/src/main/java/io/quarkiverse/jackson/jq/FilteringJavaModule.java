package io.quarkiverse.jackson.jq;

import java.util.HashMap;
import java.util.Map;

import net.thisptr.jackson.jq.v2.spi.Function;
import net.thisptr.jackson.jq.v2.spi.FunctionSignature;
import net.thisptr.jackson.jq.v2.spi.module.JavaModule;
import net.thisptr.jackson.jq.v2.spi.module.ModuleMeta;

/**
 * A {@link JavaModule} that decorates another one and only exposes the functions accepted by a {@link FunctionFilter}.
 * <p>
 * The extension generates, at build time, one subclass per enabled {@link JavaModule}. The subclass instantiates the
 * original module and keeps its {@link net.thisptr.jackson.jq.v2.spi.annotations.ModuleRegistration} annotations.
 */
public class FilteringJavaModule implements JavaModule {
    private final JavaModule delegate;
    private final Map<FunctionSignature, Function> functions;

    /**
     * @param module the module to decorate.
     * @param filter the filter that decides which functions of the module are exposed.
     * @param locator the {@link FunctionContext#getLocator() locator} given to the filter, the module id.
     */
    protected FilteringJavaModule(JavaModule module, FunctionFilter filter, String locator) {
        this.delegate = module;
        this.functions = new HashMap<>();
        for (Map.Entry<FunctionSignature, Function> entry : module.getFunctions().entrySet()) {
            if (filter.accept(FunctionContext.of(entry.getKey(), locator)))
                this.functions.put(entry.getKey(), entry.getValue());
        }
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
