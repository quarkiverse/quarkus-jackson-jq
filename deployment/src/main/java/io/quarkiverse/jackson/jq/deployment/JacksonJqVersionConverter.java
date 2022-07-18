package io.quarkiverse.jackson.jq.deployment;

import org.eclipse.microprofile.config.spi.Converter;

import io.quarkus.runtime.annotations.RegisterForReflection;
import net.thisptr.jackson.jq.Version;

@RegisterForReflection
public class JacksonJqVersionConverter implements Converter<Version> {
    @Override
    public Version convert(String value) throws IllegalArgumentException, NullPointerException {
        return Version.valueOf(value);
    }
}
