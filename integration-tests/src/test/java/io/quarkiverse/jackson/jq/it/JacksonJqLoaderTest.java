package io.quarkiverse.jackson.jq.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class JacksonJqLoaderTest {

    @Test
    public void functionsTest() {
        given().when()
                .contentType(ContentType.JSON)
                .get("/jq/functions")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("$", not(hasItem("min_by/1"))) // built-in function filtered
                .body("$", hasItem("@json/0")) // built-in function from jq.config
                .body("$", hasItem("@text/0")) // built-in function from jq.config
                .body("$", hasItem("paths/1")) // built-in function not filtered
                .body("$", hasItem("delpaths/1")) // built-in function not filtered
                .body("$", hasItem("myFunction/1")) // custom function not filtered
                .body("$", hasItem("myFunction/2")); // custom function not filtered
    }
}
