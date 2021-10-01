package io.quarkiverse.quarkus.jackson.jq.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class JacksonJqResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/jackson-jq")
                .then()
                .statusCode(200)
                .body(is("Hello jackson-jq"));
    }
}
