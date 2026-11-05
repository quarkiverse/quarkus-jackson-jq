package io.quarkiverse.jackson.jq;

/**
 * Describes a custom function discovered at build time in the application classes.
 *
 * @param name the function name, as declared in {@link net.thisptr.jackson.jq.v2.spi.annotations.FunctionRegistration}.
 * @param nargs the number of arguments of the function.
 * @param className the fully qualified name of the class implementing the function.
 * @param versionRange the string representation of the jq versions where the function is available, see
 *        {@link net.thisptr.jackson.jq.v2.spi.version.VersionRange}.
 */
public record FunctionDescriptor(String name, int nargs, String className, String versionRange) {
}
