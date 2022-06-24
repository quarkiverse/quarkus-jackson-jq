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
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.Versions;
import net.thisptr.jackson.jq.exception.JsonQueryException;

@Path("/jq")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParserResource {

    @Inject
    Scope scope;
    @Inject
    ObjectMapper mapper;

    @Path("/parse")
    @POST
    public List<JsonNode> parse(final Document document) throws JsonQueryException {
        final JsonQuery query = JsonQuery.compile(document.getExpression(), Versions.JQ_1_6);
        List<JsonNode> out = new ArrayList<>();
        query.apply(this.scope, document.getDocument(), out::add);
        return out;
    }

    @Path("/functions")
    @GET
    public ArrayNode functions() {
        Set<String> functions = new TreeSet<>();
        Scope s = this.scope;

        while (s != null) {
            functions.addAll(s.getLocalFunctions().keySet());
            s = s.getParentScope();
        }

        ArrayNode answer = mapper.createArrayNode();
        functions.forEach(answer::add);

        return answer;
    }

}
