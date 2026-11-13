package io.quarkiverse.jackson.jq;

import java.util.Set;

/**
 * Decides which functions are exposed in the jq {@link net.thisptr.jackson.jq.v2.core.Environment}.
 */
@FunctionalInterface
public interface FunctionFilter {

    /**
     * Decides whether a function must be exposed.
     *
     * @param context the information of the function to evaluate.
     * @return true if the function must be exposed, false if it must be removed.
     */
    boolean accept(FunctionContext context);

    /**
     * Creates a filter that rejects the functions with the given names, regardless of their arity and of where they
     * come from. All the other functions are accepted.
     *
     * @param names the names of the functions to reject, e.g. {@code now} or {@code @text}. The set is copied.
     * @return the filter.
     */
    static FunctionFilter denyNames(Set<String> names) {
        Set<String> denied = Set.copyOf(names);
        return context -> !denied.contains(context.getSignature().name());
    }
}
