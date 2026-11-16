package io.quarkiverse.jackson.jq.it;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

@QuarkusTest
class JacksonJqLoaderTest {

    @Test
    public void jqFunctionsTest() {
        given().when()
                .contentType(ContentType.JSON)
                .get("/jq/jqfunctions")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("$", hasItem("@json/0"))
                .body("$", not(hasItem("@text/0"))) // built-in jq function excluded
                .body("$", hasItem("_flatten/1"))
                .body("$", hasItem("_modify/2"))
                .body("$", hasItem("walk/1"))
                .body("$", hasItem("while/2"))
                .body("$", hasItem("with_entries/1"));
    }

    @Test
    public void functionsTest() {
        given().when()
                .contentType(ContentType.JSON)
                .get("/jq/functions")
                .then()
                .statusCode(200)
                .body("$", not(hasItem("min_by"))) // built-in function excluded.
                .body("size()", greaterThan(0))
                .body("$", hasItem("paths/1"))
                .body("$", hasItem("delpaths/1"))
                .body("$", hasItem("myGreet/1"))
                .body("$", hasItem("myGreet/2"));
    }

    /**
     * Ensures that the JacksonJQ environment works using any of the available injection alternatives.
     */
    @Test
    public void ensureEnvironmentExecutionTest() {
        JsonPath result = given().when()
                .get("/jq/check-environment")
                .then()
                .statusCode(200)
                .extract().jsonPath();
        assertThat(result.getObject("environmentSupplierResult", Boolean.class))
                .withFailMessage("EnvironmentSupplier invocation did not return the expected value")
                .isTrue();
        assertThat(result.getObject("environmentResult", Boolean.class))
                .withFailMessage("Environment invocation did not return the expected value")
                .isTrue();
    }
}
