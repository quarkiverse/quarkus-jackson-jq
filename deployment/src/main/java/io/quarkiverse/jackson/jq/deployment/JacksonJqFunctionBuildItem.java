package io.quarkiverse.jackson.jq.deployment;

import io.quarkus.builder.item.MultiBuildItem;
import io.quarkus.runtime.RuntimeValue;
import net.thisptr.jackson.jq.Function;

public final class JacksonJqFunctionBuildItem extends MultiBuildItem {
    private final String name;
    private final RuntimeValue<Function> function;

    public JacksonJqFunctionBuildItem(String name, RuntimeValue<Function> function) {
        this.name = name;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public RuntimeValue<Function> getFunction() {
        return function;
    }

    public static JacksonJqFunctionBuildItem of(String name, int arg, RuntimeValue<Function> function) {
        return new JacksonJqFunctionBuildItem(name + "/" + arg, function);
    }

    public static JacksonJqFunctionBuildItem of(String name, RuntimeValue<Function> function) {
        return new JacksonJqFunctionBuildItem(name, function);
    }
}
