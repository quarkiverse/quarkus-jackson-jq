package io.quarkiverse.jackson.jq;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import net.thisptr.jackson.jq.v2.spi.FunctionSignature;

/**
 * The information given to a {@link FunctionFilter} to decide whether a function must be exposed.
 */
public final class FunctionContext {
    private final FunctionSignature signature;
    private final String locator;

    private FunctionContext(FunctionSignature signature, String locator) {
        this.signature = signature;
        this.locator = locator;
    }

    /**
     * @param signature the signature (name and arity) of the function.
     * @param locator identifies where the function comes from: the import path of the module for the functions of a
     *        module, or the class name of the {@link net.thisptr.jackson.jq.v2.core.function.FunctionLoader}
     *        otherwise.
     * @return a new context.
     */
    public static FunctionContext of(FunctionSignature signature, String locator) {
        return new FunctionContext(signature, locator);
    }

    /**
     * @return the signature (name and arity) of the function.
     */
    public FunctionSignature getSignature() {
        return signature;
    }

    /**
     * @return where the function comes from, see {@link #of(FunctionSignature, String)}.
     */
    public String getLocator() {
        return locator;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (!(o instanceof FunctionContext that))
            return false;
        return Objects.equals(signature, that.signature)
                && Objects.equals(locator, that.locator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signature, locator);
    }

    @Override
    public String toString() {
        return locator + ":" + signature;
    }
}
