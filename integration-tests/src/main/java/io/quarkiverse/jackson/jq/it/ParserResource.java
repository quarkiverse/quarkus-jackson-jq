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

import net.thisptr.jackson.jq.v2.core.Environment;
import net.thisptr.jackson.jq.v2.core.JsonQuery;
import net.thisptr.jackson.jq.v2.spi.exception.JsonQueryException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

@Path("/jq")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParserResource {

    @Inject
    Environment<JsonNode> environment;
    @Inject
    ObjectMapper mapper;

    @Path("/parse")
    @POST
    public List<JsonNode> parse(final Document document) throws JsonQueryException {
        final JsonQuery<JsonNode> query = environment.compile(document.getExpression());
        List<JsonNode> out = new ArrayList<>();
        query.apply(document.getDocument(), out::add);
        return out;
    }

    @Path("/jqfunctions")
    @GET
    public ArrayNode jqFunctions() {
        List<String> functions = collectJqFunctions();
        ArrayNode answer = mapper.createArrayNode();
        functions.forEach(answer::add);
        return answer;
    }

    @Path("/functions")
    @GET
    public ArrayNode functions() {
        List<String> functions = collectFunctions();
        ArrayNode answer = mapper.createArrayNode();
        functions.forEach(answer::add);
        return answer;
    }

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
        return environment.getFunctionLoaders().stream()
                .flatMap(functionLoader -> functionLoader.getFunctions(environment.getJqVersion()).entrySet().stream())
                .map(entry -> entry.getKey().name() + "/" + entry.getKey().arity())
                .sorted().toList();
    }
}
