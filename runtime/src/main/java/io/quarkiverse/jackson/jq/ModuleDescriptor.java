package io.quarkiverse.jackson.jq;

/**
 * Describes one of the official jackson-jq extension modules known by the extension.
 *
 * @param id the module id, used in the configuration {@code quarkus.jackson.jq.modules.<id>}, e.g. {@code uuid}.
 * @param groupId the maven group id of the module artifact.
 * @param artifactId the maven artifact id of the module.
 * @param className the fully qualified name of the module class.
 * @param bundled true if the module artifact is shipped by the extension, false if it is activated by adding the
 *        dependency to the application.
 */
public record ModuleDescriptor(String id, String groupId, String artifactId, String className, boolean bundled) {
}
