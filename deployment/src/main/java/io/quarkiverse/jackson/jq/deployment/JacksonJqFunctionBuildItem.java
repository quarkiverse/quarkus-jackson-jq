package io.quarkiverse.jackson.jq.deployment;

import io.quarkiverse.jackson.jq.FunctionDescriptor;
import io.quarkus.builder.item.MultiBuildItem;

/**
 * A custom function, annotated with {@code @FunctionRegistration}, discovered in the application classes.
 */
public final class JacksonJqFunctionBuildItem extends MultiBuildItem {
    private final FunctionDescriptor functionDescriptor;

    /**
     * @param functionDescriptor the descriptor of the discovered function.
     */
    public JacksonJqFunctionBuildItem(FunctionDescriptor functionDescriptor) {
        this.functionDescriptor = functionDescriptor;
    }

    /**
     * @return the descriptor of the discovered function.
     */
    public FunctionDescriptor getFunctionDescriptor() {
        return functionDescriptor;
    }
}
