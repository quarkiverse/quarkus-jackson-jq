package io.quarkiverse.jackson.jq.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class JacksonJqParseTest {

    @Test
    public void basicParseTest() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        // join this array
        doc.setDocument(objectMapper.readTree("[\"1\", 2, 3, 4, 5, \"6\"]"));
        doc.setExpression("join(\"-\")");

        given().when()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(200)
                .body(is("[\"1-2-3-4-5-6\"]"));
    }

    // see https://github.com/quarkiverse/quarkus-jackson-jq/issues/14
    @Test
    public void sortByTest() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        // join this array
        doc.setDocument(objectMapper.readTree("{\n"
                + "   \"number\":[\n"
                + "      {\n"
                + "         \"age\":12\n"
                + "      },\n"
                + "      {\n"
                + "         \"age\":10\n"
                + "      },\n"
                + "      {\n"
                + "         \"age\":2\n"
                + "      }\n"
                + "   ]\n"
                + "}"));
        doc.setExpression(".number|sort_by(.age)");

        given().when()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(200)
                .body(is("[[{\"age\":2},{\"age\":10},{\"age\":12}]]"));
    }
}
