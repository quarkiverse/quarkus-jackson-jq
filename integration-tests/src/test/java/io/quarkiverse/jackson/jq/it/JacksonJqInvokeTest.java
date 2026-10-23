package io.quarkiverse.jackson.jq.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@QuarkusTest
class JacksonJqInvokeTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void basicInvokeTest() {
        given().when()
                .contentType(ContentType.JSON)
                .get("/jq/allfunctions")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("$", hasItem("del/1"))
                .body("$", hasItem("delpaths/1"));

        final ObjectNode node = MAPPER.createObjectNode();
        node.withObject("/commit").put("name", "Nicolas Williams");
        node.withObject("/commit").put("message", "Reject all overlong UTF8 sequences.");

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

    @Test
    public void myGreetFunctionLoadedTest() {
        given().when()
                .contentType(ContentType.JSON)
                .get("/jq/functions")
                .then()
                .statusCode(200)
                .body("$", hasItem("myGreet/1"))
                .body("$", hasItem("myGreet/2"));

    }

    @Test
    public void myGreetInEnglishWithOneParameterTest() {
        assertMyFunction("myGreet(\"Bob\")", "[\"Hello, Bob!\"]");
    }

    @Test
    public void myGreetInRequestedLanguageTest() {
        assertMyFunction("myGreet(\"EN\"; \"Bob\")", "[\"Hello, Bob!\"]");
        assertMyFunction("myGreet(\"ES\"; \"Bob\")", "[\"Hola, Bob!\"]");
        assertMyFunction("myGreet(\"IT\"; \"Bob\")", "[\"Ciao, Bob!\"]");
    }

    @Test
    public void myGreetUnknownLanguageFallsBackToSpanishTest() {
        assertMyFunction("myGreet(\"FR\"; \"Bob\")", "[\"Hello, Bob!\"]");
    }

    private static void assertMyFunction(String expression, String expectedBody) {
        final Document doc = new Document();
        doc.setDocument(MAPPER.createObjectNode());
        doc.setExpression(expression);

        given().when()
                .contentType(ContentType.JSON)
                .body(MAPPER.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(200)
                .body(is(expectedBody));
    }
}
