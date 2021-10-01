package io.quarkiverse.jackson.jq;

import java.util.Map;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import net.thisptr.jackson.jq.BuiltinFunctionLoader;
import net.thisptr.jackson.jq.Function;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;

@Recorder
public class JacksonJqScopeRecorder {

    public RuntimeValue<Scope> createScope(final Map<String, Function> functions) {
        final Scope scope = Scope.newEmptyScope();
        functions.forEach(scope::addFunction);
        BuiltinFunctionLoader.getInstance().loadFunctionsFromJsonJq(
                BuiltinFunctionLoader.class.getClassLoader(),
                Versions.JQ_1_6,
                scope).forEach(scope::addFunction);
        return new RuntimeValue<>(scope);
    }
}
