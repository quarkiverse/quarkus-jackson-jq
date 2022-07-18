package io.quarkiverse.jackson.jq.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class JacksonJqInvokeTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void basicInvokeTest() throws JsonProcessingException {
        given().when()
                .contentType(ContentType.JSON)
                .get("/jq/functions")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("$", hasItem("del/1"))
                .body("$", hasItem("delpaths/1"));

        final ObjectNode node = MAPPER.createObjectNode();
        node.with("commit").put("name", "Nicolas Williams");
        node.with("commit").put("message", "Reject all overlong UTF8 sequences.");

        final Document doc = new Document();
        doc.setDocument(node);
        doc.setExpression("del(.commit.name)");

        given().when()
                .contentType(ContentType.JSON)
                .body(MAPPER.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(200)
                .body(is("[{\"commit\":{\"message\":\"Reject all overlong UTF8 sequences.\"}}]"));
    }
}
