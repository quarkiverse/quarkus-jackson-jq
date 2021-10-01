package io.quarkiverse.jackson.jq.deployment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import io.quarkiverse.jackson.jq.JacksonJqScopeRecorder;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourcePatternsBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.recording.RecorderContext;
import net.thisptr.jackson.jq.BuiltinFunctionLoader;
import net.thisptr.jackson.jq.Function;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;

class JacksonJqProcessor {

    private static final String FEATURE = "jackson-jq";
    private static final String REFLECTION_VERSION_RANGE_DESERIALIZER = "net.thisptr.jackson.jq.internal.misc.VersionRangeDeserializer";
    private static final String JSON_CONFIG_GLOB = "net/thisptr/jackson/**/*.json";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    List<ReflectiveClassBuildItem> registerForReflection() {
        final List<ReflectiveClassBuildItem> reflectionConfig = new ArrayList<>();
        reflectionConfig.add(ReflectiveClassBuildItem.weakClass(REFLECTION_VERSION_RANGE_DESERIALIZER));
        return reflectionConfig;
    }

    @BuildStep
    NativeImageResourcePatternsBuildItem includeJsonConfigFile() {
        return NativeImageResourcePatternsBuildItem.builder().includeGlob(JSON_CONFIG_GLOB).build();
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    SyntheticBeanBuildItem quarkusScopeBean(JacksonJqScopeRecorder recorder,
            RecorderContext context) throws NoSuchMethodException {
        // preload everything and use it as a parent scope
        final Map<String, Function> functions = BuiltinFunctionLoader.getInstance()
                .loadFunctionsFromServiceLoader(BuiltinFunctionLoader.class.getClassLoader(), Versions.JQ_1_6);
        return SyntheticBeanBuildItem
                .configure(Scope.class)
                .scope(Singleton.class)
                .runtimeValue(recorder.createScope(functions))
                .done();
    }
}
