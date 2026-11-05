package io.quarkiverse.jackson.jq;

import java.util.List;

/**
 * Closed set of the official jackson-jq extension modules the extension knows about.
 * <p>
 * Only modules listed here can be activated. Class names are kept as strings on purpose, so this registry can be
 * used even when the module artifacts are not on the classpath.
 * <p>
 * Versions are intentionally not stored: they are aligned with {@code ${jackson-jq.version}}.
 */
public final class JacksonJqModuleRegistry {

    public static final String GROUP_ID = "net.thisptr.jackson.jq.v2";
    public static final String JONI_MODULE_ARTIFACT_ID = "jackson-jq-ext-module-joni";
    public static final String RE2_MODULE_ARTIFACT_ID = "jackson-jq-ext-module-re2";

    private static final List<ModuleDescriptor> MODULES = List.of(
            // Bundled by default: safe, in-memory execution.
            new ModuleDescriptor("uuid", GROUP_ID, "jackson-jq-ext-module-uuid",
                    "net.thisptr.jackson.jq.v2.ext.uuid.UuidModule", true),
            new ModuleDescriptor("time", GROUP_ID, "jackson-jq-ext-module-time",
                    "net.thisptr.jackson.jq.v2.ext.time.TimeModule", true),
            new ModuleDescriptor("uri", GROUP_ID, "jackson-jq-ext-module-uri",
                    "net.thisptr.jackson.jq.v2.ext.uri.UriModule", true),
            new ModuleDescriptor("random", GROUP_ID, "jackson-jq-ext-module-random",
                    "net.thisptr.jackson.jq.v2.ext.random.RandomModule", true),
            new ModuleDescriptor("joni", GROUP_ID, "jackson-jq-ext-module-joni",
                    "net.thisptr.jackson.jq.v2.ext.joni.JoniRegexModule", true),
            // Activated by adding the dependency to the application.
            new ModuleDescriptor("binary", GROUP_ID, "jackson-jq-ext-module-binary",
                    "net.thisptr.jackson.jq.v2.ext.binary.BinaryModule", false),
            new ModuleDescriptor("debug", GROUP_ID, "jackson-jq-ext-module-debug",
                    "net.thisptr.jackson.jq.v2.ext.debug.DebugModule", false),
            new ModuleDescriptor("fs", GROUP_ID, "jackson-jq-ext-module-fs",
                    "net.thisptr.jackson.jq.v2.ext.fs.FsModule", false),
            new ModuleDescriptor("gzip", GROUP_ID, "jackson-jq-ext-module-gzip",
                    "net.thisptr.jackson.jq.v2.ext.gzip.GzipModule", false),
            new ModuleDescriptor("http", GROUP_ID, "jackson-jq-ext-module-http",
                    "net.thisptr.jackson.jq.v2.ext.http.HttpModule", false),
            new ModuleDescriptor("os", GROUP_ID, "jackson-jq-ext-module-os",
                    "net.thisptr.jackson.jq.v2.ext.os.OsModule", false),
            new ModuleDescriptor("re2", GROUP_ID, "jackson-jq-ext-module-re2",
                    "net.thisptr.jackson.jq.v2.ext.re2.Re2RegexModule", false),
            new ModuleDescriptor("zstd", GROUP_ID, "jackson-jq-ext-module-zstd",
                    "net.thisptr.jackson.jq.v2.ext.zstd.ZstdModule", false));

    private JacksonJqModuleRegistry() {
    }

    /**
     * @param id the module id, e.g. {@code uuid}.
     * @return the module descriptor, or {@code null} if the id is not a known module.
     */
    public static ModuleDescriptor findById(String id) {
        return MODULES.stream().filter(m -> m.id().equals(id)).findFirst().orElse(null);
    }

    /**
     * @param artifactId the artifact id of the module, e.g. {@code jackson-jq-ext-module-uuid}.
     * @return the module descriptor, or {@code null} if the artifact is not a known module.
     */
    public static ModuleDescriptor findByArtifactId(String artifactId) {
        return MODULES.stream().filter(m -> m.artifactId().equals(artifactId)).findFirst().orElse(null);
    }
}
