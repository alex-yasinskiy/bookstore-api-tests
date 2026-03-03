package com.bookstore.handlers;

import com.bookstore.models.AuthorBody;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static java.lang.String.format;

public class AuthorHandler extends BaseAbstractHandler {
    private static final String AUTHORS = "Authors";
    private static final String AUTHOR_ID = "Authors/%s";

    public static final String PATH_SCHEMA_AUTHORS_RESPONSE = "schemas/AuthorsResponseSchema.json";
    public static final String PATH_SCHEMA_AUTHOR_RESPONSE = "schemas/AuthorResponseSchema.json";

    @Step("Get list of authors")
    public Response getAuthors() {
        return RestAssured.given()
                .spec(requestSpecification)
                .get(AUTHORS);
    }

    @Step("Create author with following details - {0}")
    public Response createAuthor(AuthorBody authorBody) {
        return RestAssured.given()
                .spec(requestSpecification)
                .body(authorBody)
                .post(AUTHORS);
    }

    @Step("Delete author by ID - {0}")
    public Response deleteAuthor(String authorId) {
        return RestAssured.given()
                .spec(requestSpecification)
                .delete(format(AUTHOR_ID, authorId));
    }

    @Step("Get author by ID - {0}")
    public Response getAuthorById(String authorId) {
        return RestAssured.given()
                .spec(requestSpecification)
                .get(format(AUTHOR_ID, authorId));
    }

    @Step("Update author with following parameters - {0}")
    public Response updateAuthor(AuthorBody authorBody) {
        return RestAssured.given()
                .spec(requestSpecification)
                .body(authorBody)
                .put(format(AUTHOR_ID, authorBody.getId().toString()));
    }
}
