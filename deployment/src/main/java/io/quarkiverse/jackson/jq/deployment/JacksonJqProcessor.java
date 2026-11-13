package io.quarkiverse.jackson.jq.deployment;

import java.util.List;

import jakarta.inject.Singleton;

import io.quarkiverse.jackson.jq.EnvironmentProducer;
import io.quarkiverse.jackson.jq.EnvironmentSupplier;
import io.quarkiverse.jackson.jq.internal.JacksonJqEnvironmentCreator;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.core.Phase;
import io.quarkus.core.deployment.service.ServiceRegistrar;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import net.thisptr.jackson.jq.v2.core.Environment;
import net.thisptr.jackson.jq.v2.spi.Function;
import net.thisptr.jackson.jq.v2.spi.JqLibrary;
import net.thisptr.jackson.jq.v2.spi.module.Module;
import tools.jackson.databind.JsonNode;

/**
 * Build steps of the jackson-jq extension.
 */
class JacksonJqProcessor {
    private static final String FEATURE = "jackson-jq";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    /**
     * Registers the jq {@code Environment} as a bean. The environment is created during the static init phase, using
     * the configured jq version and excluded functions, and held by a synthetic {@code EnvironmentSupplier} bean. The
     * {@code EnvironmentProducer} exposes it as an injectable {@code Environment<JsonNode>}.
     */
    @BuildStep
    void quarkusEnvironmentBean(
            JacksonJqConfig config,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeansBuildItemProducer,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans,
            ServiceRegistrar serviceRegistrar) {

        String version = config.functions().version().toString();
        List<String> excludes = List.copyOf(config.functions().excludes().orElse(List.of()));
        serviceRegistrar.forService(EnvironmentSupplier.class, "environment-supplier")
                .atPhase(Phase.STATIC_INIT)
                .onStart((startContext) -> {
                    Environment<JsonNode> env = JacksonJqEnvironmentCreator.createEnvironment(version, excludes);
                    return new EnvironmentSupplier(env);
                });

        syntheticBeansBuildItemProducer.produce(SyntheticBeanBuildItem.configure(EnvironmentSupplier.class)
                .serviceValue(EnvironmentSupplier.class, "environment-supplier")
                .scope(Singleton.class)
                .done());

        additionalBeans.produce(AdditionalBeanBuildItem.unremovableOf(EnvironmentProducer.class));
    }

    /**
     * Keeps the {@code Environment} bean even if the application does not inject it by type.
     */
    @BuildStep
    UnremovableBeanBuildItem unremovableBeans() {
        return UnremovableBeanBuildItem.beanTypes(Environment.class);
    }

    /**
     * Registers for native image all the {@code Function}, {@code JqLibrary} and {@code Module} service providers
     * found in the classpath, since jackson-jq discovers them with {@link java.util.ServiceLoader}.
     */
    @BuildStep
    void registerModulesForNative(BuildProducer<ServiceProviderBuildItem> serviceProviders) {
        serviceProviders.produce(ServiceProviderBuildItem.allProvidersFromClassPath(Function.class.getName()));
        serviceProviders.produce(ServiceProviderBuildItem.allProvidersFromClassPath(JqLibrary.class.getName()));
        serviceProviders.produce(ServiceProviderBuildItem
                .allProvidersFromClassPath(Module.class.getName()));
    }
}
