package io.quarkiverse.jackson.jq.deployment;

import jakarta.inject.Singleton;

import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.Type;

import io.quarkiverse.jackson.jq.JacksonJqEnvironmentRecorder;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import io.quarkus.runtime.RuntimeValue;
import net.thisptr.jackson.jq.v2.core.Environment;
import net.thisptr.jackson.jq.v2.spi.Function;
import net.thisptr.jackson.jq.v2.spi.JqLibrary;
import net.thisptr.jackson.jq.v2.spi.module.Module;
import tools.jackson.databind.JsonNode;

class JacksonJqProcessor {
    private static final String FEATURE = "jackson-jq";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    SyntheticBeanBuildItem quarkusEnvironmentBean(
            JacksonJqConfig config,
            JacksonJqEnvironmentRecorder recorder) {
        RuntimeValue<Environment<JsonNode>> environment = recorder.createEnvironment(config.functions().version().toString());
        DotName envDotName = DotName.createSimple(Environment.class.getName());
        DotName jsonNodeDotName = DotName.createSimple(JsonNode.class.getName());
        Type jsonNodeType = ClassType.create(jsonNodeDotName, Type.Kind.CLASS);
        ParameterizedType envType = ParameterizedType.create(envDotName, new Type[] { jsonNodeType }, null);

        return SyntheticBeanBuildItem
                .configure(envDotName)
                .addType(envType)
                .scope(Singleton.class)
                .runtimeValue(environment)
                .done();
    }

    @BuildStep
    UnremovableBeanBuildItem unremovableBeans() {
        return UnremovableBeanBuildItem.beanTypes(Environment.class);
    }

    @BuildStep
    void registerModulesForNative(BuildProducer<ServiceProviderBuildItem> serviceProviders) {
        serviceProviders.produce(ServiceProviderBuildItem.allProvidersFromClassPath(Function.class.getName()));
        serviceProviders.produce(ServiceProviderBuildItem.allProvidersFromClassPath(JqLibrary.class.getName()));
        serviceProviders.produce(ServiceProviderBuildItem
                .allProvidersFromClassPath(Module.class.getName()));
    }
}
