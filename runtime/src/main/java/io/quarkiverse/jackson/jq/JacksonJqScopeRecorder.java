package io.quarkiverse.jackson.jq;

import java.util.List;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import net.thisptr.jackson.jq.Function;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Version;
import net.thisptr.jackson.jq.internal.IsolatedScopeQuery;
import net.thisptr.jackson.jq.internal.JsonQueryFunction;
import net.thisptr.jackson.jq.internal.javacc.ExpressionParser;

@Recorder
public class JacksonJqScopeRecorder {

    public RuntimeValue<Scope> createScope() {
        final Scope scope = Scope.newEmptyScope();
        return new RuntimeValue<>(scope);
    }

    public RuntimeValue<Scope> createScope(RuntimeValue<Scope> parent) {
        final Scope scope = Scope.newChildScope(parent.getValue());
        return new RuntimeValue<>(scope);
    }

    public void addFunction(RuntimeValue<Scope> scope, String name, Function function) {
        scope.getValue().addFunction(name, function);
    }

    public void addFunction(RuntimeValue<Scope> scope, String name, RuntimeValue<Function> function) {
        scope.getValue().addFunction(name, function.getValue());
    }

    public void addFunction(RuntimeValue<Scope> scope, String name, List<String> args, String body, String version) {
        try {
            Function function = new JsonQueryFunction(
                    name,
                    args,
                    new IsolatedScopeQuery(ExpressionParser.compile(body, Version.valueOf(version))),
                    scope.getValue());

            scope.getValue().addFunction(
                    name,
                    args.size(),
                    function);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
