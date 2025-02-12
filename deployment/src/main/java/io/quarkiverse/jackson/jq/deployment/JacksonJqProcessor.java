package io.quarkiverse.jackson.jq.deployment;

import static io.quarkiverse.jackson.jq.deployment.JacksonJqSupport.lookupFunctions;
import static io.quarkiverse.jackson.jq.deployment.JacksonJqSupport.lookupFunctionsFromConfig;

import java.util.Collections;
import java.util.List;

import jakarta.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.jboss.jandex.IndexView;

import io.quarkiverse.jackson.jq.JacksonJqScopeRecorder;
import io.quarkiverse.jackson.jq.JqFunction;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.ApplicationArchivesBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.recording.RecorderContext;
import io.quarkus.runtime.RuntimeValue;
import net.thisptr.jackson.jq.BuiltinFunction;
import net.thisptr.jackson.jq.Scope;

class JacksonJqProcessor {
    private static final String FEATURE = "jackson-jq";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    SyntheticBeanBuildItem quarkusScopeBean(
            CombinedIndexBuildItem combinedIndex,
            ApplicationArchivesBuildItem archives,
            JacksonJqConfig config,
            JacksonJqScopeRecorder recorder,
            List<JacksonJqFunctionBuildItem> functions,
            RecorderContext context) throws Exception {

        IndexView indexView = combinedIndex.getIndex();
        List<String> excludes = config.functions().excludes().orElseGet(Collections::emptyList);

        RuntimeValue<Scope> root = recorder.createScope();
        RuntimeValue<Scope> local = recorder.createScope(root);

        // load built-in int functions
        lookupFunctionsFromConfig(archives, config).forEach(e -> {
            if (!excludes.contains(e.name)) {
                recorder.addFunction(root, e.name, e.args, e.body, config.functions().version().toString());
            }
        });
        lookupFunctions(indexView, config, context, BuiltinFunction.class).forEach(f -> {
            if (!excludes.contains(StringUtils.substringBefore(f.getName(), '/'))) {
                recorder.addFunction(root, f.getName(), f.getFunction());
            }
        });
        lookupFunctions(indexView, config, context, net.thisptr.jackson.jq.internal.BuiltinFunction.class).forEach(f -> {
            if (!excludes.contains(StringUtils.substringBefore(f.getName(), '/'))) {
                recorder.addFunction(root, f.getName(), f.getFunction());
            }
        });

        // load custom function
        lookupFunctions(indexView, config, context, JqFunction.class).forEach(f -> {
            if (!excludes.contains(StringUtils.substringBefore(f.getName(), '/'))) {
                recorder.addFunction(local, f.getName(), f.getFunction());
            }
        });
        functions.forEach(f -> {
            if (!excludes.contains(StringUtils.substringBefore(f.getName(), '/'))) {
                recorder.addFunction(local, f.getName(), f.getFunction());
            }
        });

        return SyntheticBeanBuildItem
                .configure(Scope.class)
                .scope(Singleton.class)
                .runtimeValue(local)
                .done();
    }

    @BuildStep
    UnremovableBeanBuildItem unremovableBeans() {
        return UnremovableBeanBuildItem.beanTypes(Scope.class);
    }
}
