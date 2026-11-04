package io.quarkiverse.jackson.jq;

import java.util.Set;

/**
 * Decides which functions are exposed in the jq {@link net.thisptr.jackson.jq.v2.core.Environment}.
 */
@FunctionalInterface
public interface FunctionFilter {

    /**
     * @param context the information of the function to evaluate.
     * @return true if the function must be exposed, false if it must be removed.
     */
    boolean accept(FunctionContext context);

    /**
     * Creates a filter that rejects the functions with the given names, regardless of their arity.
     *
     * @param names the names of the functions to reject, e.g. {@code now} or {@code @text}.
     * @return the filter.
     */
    static FunctionFilter denyNames(Set<String> names) {
        Set<String> denied = Set.copyOf(names);
        return context -> !denied.contains(context.getSignature().name());
    }
}
