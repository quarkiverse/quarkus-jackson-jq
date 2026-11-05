package io.quarkiverse.jackson.jq.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

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
                .body("$", not(hasItem("@text/0"))) // built-in function filtered from (BuiltinFunctionLoader / CoreJqLibrary)
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
                .body("size()", greaterThan(0))
                .body("$", hasItem("paths/1"))
                .body("$", hasItem("delpaths/1"))
                .body("$", hasItem("myGreet/1"))
                .body("$", hasItem("myGreet/2"));
    }
}
