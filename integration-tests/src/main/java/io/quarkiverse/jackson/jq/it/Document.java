package io.quarkiverse.jackson.jq.it;

import tools.jackson.databind.JsonNode;

/**
 * Request payload of the test resource: a jq expression and the JSON document to evaluate it on.
 */
public class Document {

    private String expression;
    private JsonNode document;

    public JsonNode getDocument() {
        return document;
    }

    public void setDocument(JsonNode document) {
        this.document = document;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
