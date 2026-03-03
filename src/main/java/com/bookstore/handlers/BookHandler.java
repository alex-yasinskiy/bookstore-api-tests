package com.bookstore.handlers;

import com.bookstore.models.BookBody;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static java.lang.String.format;

public class BookHandler extends BaseAbstractHandler {

    private static final String BOOKS = "Books";
    private static final String BOOK_ID = "Books/%s";

    public static final String PATH_SCHEMA_BOOKS_RESPONSE = "schemas/BooksResponseSchema.json";
    public static final String PATH_SCHEMA_BOOK_RESPONSE = "schemas/BookResponseSchema.json";

    @Step("Get list of books")
    public Response getBooks() {
        return RestAssured.given()
                .spec(requestSpecification)
                .get(BOOKS);
    }

    @Step("Create book with following details - {0}")
    public Response createBook(BookBody bookBody) {
        return RestAssured.given()
                .spec(requestSpecification)
                .body(bookBody)
                .post(BOOKS);
    }

    @Step("Delete book by ID - {0}")
    public Response deleteBook(String bookId) {
        return RestAssured.given()
                .spec(requestSpecification)
                .delete(format(BOOK_ID, bookId));
    }

    @Step("Get book by ID - {0}")
    public Response getBookById(String bookId) {
        return RestAssured.given()
                .spec(requestSpecification)
                .get(format(BOOK_ID, bookId));
    }

    @Step("Update book with following parameters - {0}")
    public Response updateBook(BookBody bookBody) {
        return RestAssured.given()
                .spec(requestSpecification)
                .body(bookBody)
                .put(format(BOOK_ID, bookBody.getId().toString()));
    }
}
