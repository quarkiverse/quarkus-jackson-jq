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
     * Example of BuiltinFunction functions usage.
     * BuiltinFunctions are always present and used as is, don't require any other configuration like strategy, path, or alias.
     */
    @Test
    void builtinFunctionTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.readTree("{}"));
        doc.setExpression("123 | localtime");

        List<String> result = given().when()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().get("");

        assertThat(result).hasSize(1);
    }

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

    /**
     * Example of jackson-jq-ext-module-uuid extension module usage via expression import.
     * 1) Since no quarkus.jackson.jq.modules.uuid.strategy is configured, the default "register" strategy is applied.
     * 2) Since no quarkus.jackson.jq.modules.uuid.path is configured, the default value "jackson-jq/uuid" provided
     * by the jackson-jq library is applied.
     */
    @Test
    void uuidModuleExpressionImportTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.readTree("{}"));
        // import the module by using the by default module path "jackson-jq/uuid".
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
     * Example of jackson-jq-ext-module-uuid extension module usage via expression include.
     * 1) Since no quarkus.jackson.jq.modules.uuid.strategy is configured, the default "register" strategy is applied.
     * 2) Since no quarkus.jackson.jq.modules.uuid.path is configured, the default value "jackson-jq/uuid" provided
     * by the jackson-jq library is applied.
     */
    @Test
    void uuidModuleExpressionIncludeTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.readTree("{}"));
        // include the module by using the by default module path "jackson-jq/uuid".
        String INCLUDE = "include \"jackson-jq/uuid\"; ";
        doc.setExpression(INCLUDE + "uuid4");

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
     * 1) Since no quarkus.jackson.jq.modules.uuid.strategy is configured, the default "register" strategy is applied.
     * 2) Since no quarkus.jackson.jq.modules.uuid.path is configured, the default value "jackson-jq/uuid" provided
     * by the jackson-jq library is applied.
     * However, the invocation must fail.
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
     * Example of jackson-jq-ext-module-time extension module usage via expression import when an alternative path is
     * configured.
     * 1) Since no quarkus.jackson.jq.modules.time.strategy is configured, the default "register" strategy is applied.
     * 2) Since the quarkus.jackson.jq.modules.time.path=jackson-jq/time-example-path, was provided, the module is
     * registered under the path "jackson-jq/time-example-path".
     */
    @Test
    void timeModuleExpressionImportWithConfiguredPathTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.readTree("{}"));
        // import the module by using the user provided registration path "jackson-jq/time-example-path".
        String IMPORT = "import \"jackson-jq/time-example-path\" as time; ";

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
     * Example of jackson-jq-ext-module-time extension module usage via expression include when an alternative path is
     * configured.
     * 1) Since no quarkus.jackson.jq.modules.time.strategy is configured, the default "register" strategy is applied.
     * 2) Since the quarkus.jackson.jq.modules.time.path=jackson-jq/time-example-path, was provided, the module is
     * registered under the path "jackson-jq/time-example-path".
     */
    @Test
    void timeModuleExpressionIncludeWithConfiguredPathTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.readTree("{}"));
        // import the module by using the user provided registration path "jackson-jq/time-example-path".
        String INCLUDE = "include \"jackson-jq/time-example-path\"; ";

        doc.setExpression(INCLUDE + "1792152626431 | strftime(\"yyyy-MM-dd HH:mm:ss.SSSXXX\"; \"UTC\")");
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
     * Example of jackson-jq-ext-module-uri extension module usage when configured with the "import" strategy.
     * 1) Since the quarkus.jackson.jq.modules.uri.strategy=import is configured, the alias is required.
     * 2) Since the quarkus.jackson.jq.modules.uri.alias=urialias was configured, the module can be used directly in
     * the queries without the need of an "import" sentence by using the alias value.
     */
    @Test
    void uriModuleImportTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.readTree("{}"));
        // no need to import the module, it's already import on every query and can be used directly as urialias::func(...)
        doc.setExpression(
                "\"http://bob@example.com:8080/pets\" | uriexamplealias::uriparse | [.scheme, .user_info, .host, .port, .path]");

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
     * Example of jackson-jq-ext-module-random extension module usage when configured with the "include" strategy.
     * 1) Since the quarkus.jackson.jq.modules.random.strategy=include is configured, the module functions
     * can be used directly on every query with the given name, e.g. myfunc(...).
     */
    @Test
    void randomModuleIncludeTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.readTree("{}"));
        // no need to import, nor include, nor use any alias. The module is already imported on every query.
        doc.setExpression("123 | random");

        List<List<Object>> result = given().when()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().get("");

        assertThat(result).hasSize(1);
        Object value = result.getFirst();
        assertThat(value).isInstanceOf(Float.class);
    }

    /**
     * Example of jackson-jq-ext-module-joni extension module usage.
     */
    @Test
    void joniRegexModuleTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.createObjectNode().stringNode("aba"));
        // The module is automatically shipped by the extension as maven dependency. No additional configuration is
        // set in the application.properties so the default strategy = register is applied. And thus, the module is
        // registered in the Environment with the default path "jackson-jq/joni". We must either "import"
        // or "include" in the expression for usage.
        String IMPORT = "import \"jackson-jq/joni\" as joni; ";
        doc.setExpression(IMPORT + "joni::splits((\"a\", \"b\"))");

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
     * Example of jackson-jq-ext-module-os extension module usage.
     */
    @Test
    void useDependencyAddedModuleTest() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Document doc = new Document();
        doc.setDocument(objectMapper.createObjectNode().stringNode("aba"));
        // The module was incorporated to the project with an explicit maven dependency by not providing any strategy configuration.
        // No additional configuration is set in the application.properties so the default strategy = register is applied.
        // And thus, the module is registered in the Environment with the default path "jackson-jq/joni". We must
        // either "import" or "include" in the expression for usage.

        String IMPORT = "import \"jackson-jq/os\" as os; ";
        doc.setExpression(IMPORT + "os::hostname");

        List<Object> result = given().when()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(doc))
                .post("/jq/parse")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().get("");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst())
                .isNotNull()
                .isInstanceOf(String.class);
    }
}
