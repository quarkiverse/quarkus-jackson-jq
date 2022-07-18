package io.quarkiverse.jackson.jq.it;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import io.quarkiverse.jackson.jq.JqFunction;
import net.thisptr.jackson.jq.Expression;
import net.thisptr.jackson.jq.Function;
import net.thisptr.jackson.jq.PathOutput;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Version;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.jackson.jq.path.Path;

@JqFunction({ "myFunction/1", "myFunction/2" })
public class MyFunction implements Function {
    @Override
    public void apply(Scope scope, List<Expression> args, JsonNode in, Path path, PathOutput output, Version version)
            throws JsonQueryException {
        throw new JsonQueryException("Not Yet Implemented");
    }
}
