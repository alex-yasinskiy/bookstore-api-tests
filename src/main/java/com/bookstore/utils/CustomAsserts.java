package com.bookstore.utils;

import io.restassured.response.Response;
import org.hamcrest.Matchers;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static com.bookstore.utils.Utils.logAllureStep;
import static org.hamcrest.MatcherAssert.assertThat;

public class CustomAsserts {

    public static <T> void assertCode(T actual, Integer code) {
        logAllureStep("Verify response code is equal to - " + code);
        assertThat("Invalid status code",
                actual,
                Matchers.equalTo(code));
    }

    public static void assertSchema(String reason, Response response, String schema) {
        logAllureStep("Verify response is matching following schema - " + schema);
        assertThat(
                reason,
                response.getBody().asString(),
                matchesJsonSchemaInClasspath(schema));
    }
}
