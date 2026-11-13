package io.quarkiverse.jackson.jq.it;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import tools.jackson.databind.ObjectMapper;

@QuarkusTest
class JacksonJqExtensionModulesTest {

    /**
     * Example of jackson-jq-ext-module-uuid extension module usage.
     */
    @Test
    void uuidModuleTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.readTree("{}"));
        String IMPORT = "import \"jackson-jq/uuid\" as uuid; ";
        doc.setExpression(IMPORT + "uuid::uuid4");

        int expectedUUID64Size = "38941dd7-7d53-4855-ab72-2e2613ce9511".length();

        List<String> result = given().when()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().get("");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).hasSize(expectedUUID64Size);
    }

    /**
     * Example of invocation of an excluded function in the jackson-jq-ext-module-uuid extension module.
     * The invocation must fail since the "uuid3" function was excluded, see application.properties.
     */
    @Test
    void uuidModuleExcludedFunctionTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.readTree("{}"));
        // import the module by using the by default module path "jackson-jq/uuid".
        String IMPORT = "import \"jackson-jq/uuid\" as uuid; ";
        doc.setExpression(IMPORT + " \"example.com\" | uuid::uuid3(\"6ba7b810-9dad-11d1-80b4-00c04fd430c8\")");

        // The invocation will fail with code 500 since the uuid3 function is excluded.
        given().when()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(500);
    }

    /**
     * Example of jackson-jq-ext-module-time extension module usage.
     */
    @Test
    void timeModuleTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.readTree("{}"));
        String IMPORT = "import \"jackson-jq/time\" as time; ";

        doc.setExpression(IMPORT + "1792152626431 | time::strftime(\"yyyy-MM-dd HH:mm:ss.SSSXXX\"; \"UTC\")");
        String expectedTime = "2026-10-16 12:10:26.431Z";

        List<String> result = given().when()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().get("");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(expectedTime);
    }

    /**
     * Example of jackson-jq-ext-module-uri extension module usage.
     */
    @Test
    void uriModuleTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.readTree("{}"));
        String IMPORT = "import \"jackson-jq/uri\" as uri; ";
        doc.setExpression(
                IMPORT + "\"http://bob@example.com:8080/pets\" | uri::uriparse | [.scheme, .user_info, .host, .port, .path]");

        List<List<Object>> result = given().when()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().get("");

        assertThat(result).hasSize(1);
        List<Object> values = result.getFirst();
        assertThat(values).hasSize(5);
        assertThat(values).element(0).isEqualTo("http");
        assertThat(values).element(1).isEqualTo("bob");
        assertThat(values).element(2).isEqualTo("example.com");
        assertThat(values).element(3).hasToString("8080");
        assertThat(values).element(4).isEqualTo("/pets");
    }

    /**
     * Example of jackson-jq-regex-impl-joni extension module usage.
     */
    @Test
    void joniRegexModuleTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.createObjectNode().stringNode("aba"));
        doc.setExpression("splits((\"a\", \"b\"))");

        List<Object> result = given().when()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().get("");

        assertThat(result).hasSize(5);
        assertThat(result).hasSize(5);
        assertThat(result).element(0).isEqualTo("");
        assertThat(result).element(1).isEqualTo("b");
        assertThat(result).element(2).isEqualTo("");
        assertThat(result).element(3).isEqualTo("a");
        assertThat(result).element(4).isEqualTo("a");
    }

    /**
     * Example of invocation of an excluded built-in function.
     * The invocation must fail since the "now" function was excluded, see application.properties.
     */
    @Test
    void builtInExcludedFunctionTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.readTree("{}"));
        doc.setExpression("123 | now");

        // The invocation will fail with code 500 since the now function is excluded.
        given().when()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(500);
    }
}
