package io.quarkiverse.jackson.jq.it;

import com.fasterxml.jackson.databind.JsonNode;

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
