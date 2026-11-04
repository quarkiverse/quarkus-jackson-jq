/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkiverse.jackson.jq.it;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jspecify.annotations.NonNull;

import io.quarkiverse.jackson.jq.EnvironmentSupplier;
import net.thisptr.jackson.jq.v2.core.Environment;
import net.thisptr.jackson.jq.v2.core.JsonQuery;
import net.thisptr.jackson.jq.v2.spi.exception.JsonQueryException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

/**
 * Test resource that evaluates jq expressions with the {@link Environment} created by the extension, and lists the
 * functions available in it.
 */
@Path("/jq")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParserResource {

    @Inject
    EnvironmentSupplier environmentSupplier;

    @Inject
    Environment<JsonNode> environment;

    @Inject
    ObjectMapper mapper;

    /**
     * @param document the jq expression and the JSON document to evaluate it on.
     * @return the outputs of the expression.
     * @throws JsonQueryException if the expression is invalid or fails, e.g. it invokes an excluded function.
     */
    @Path("/parse")
    @POST
    public List<JsonNode> parse(final Document document) throws JsonQueryException {
        final JsonQuery<JsonNode> query = environmentSupplier.get().compile(document.getExpression());
        List<JsonNode> out = new ArrayList<>();
        query.apply(document.getDocument(), out::add);
        return out;
    }

    /**
     * @return the {@code name/arity} of the functions defined in jq source code.
     */
    @Path("/jqfunctions")
    @GET
    public ArrayNode jqFunctions() {
        List<String> functions = collectJqFunctions();
        ArrayNode answer = mapper.createArrayNode();
        functions.forEach(answer::add);
        return answer;
    }

    /**
     * @return the {@code name/arity} of the functions implemented in Java.
     */
    @Path("/functions")
    @GET
    public ArrayNode functions() {
        List<String> functions = collectFunctions();
        ArrayNode answer = mapper.createArrayNode();
        functions.forEach(answer::add);
        return answer;
    }

    /**
     * @return the sorted {@code name/arity} of all the functions, both jq and Java.
     */
    @Path("/allfunctions")
    @GET
    public ArrayNode allFunctions() {
        List<String> allFunctions = new ArrayList<>(collectJqFunctions());
        allFunctions.addAll(collectFunctions());
        allFunctions = allFunctions.stream().sorted().toList();
        ArrayNode answer = mapper.createArrayNode();
        allFunctions.forEach(answer::add);
        return answer;
    }

    private @NonNull List<String> collectJqFunctions() {
        return environment.getFunctionLoaders().stream()
                .flatMap(functionLoader -> functionLoader.getJqFunctions(environment.getJqVersion()).entrySet().stream())
                .map(entry -> entry.getKey().name() + "/" + entry.getKey().arity())
                .sorted().toList();
    }

    private @NonNull List<String> collectFunctions() {
        return environmentSupplier.get().getFunctionLoaders().stream()
                .flatMap(functionLoader -> functionLoader.getFunctions(environmentSupplier.get().getJqVersion()).entrySet()
                        .stream())
                .map(entry -> entry.getKey().name() + "/" + entry.getKey().arity())
                .sorted().toList();
    }
}
