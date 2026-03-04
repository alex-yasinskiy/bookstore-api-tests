package com.bookstore;

import com.bookstore.handlers.BookHandler;
import com.bookstore.models.BookBody;
import com.bookstore.utils.DataFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.bookstore.handlers.BookHandler.PATH_SCHEMA_BOOKS_RESPONSE;
import static com.bookstore.handlers.BookHandler.PATH_SCHEMA_BOOK_RESPONSE;
import static com.bookstore.utils.CustomAsserts.assertCode;
import static com.bookstore.utils.CustomAsserts.assertSchema;
import static com.bookstore.utils.Utils.logAllureStep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Bookstore API")
@Feature("Books Management - negative tests")
public class BookApiNegativeTests {

    private BookHandler bookHandler;
    private List<String> booksToCleanUp;
    private BookBody preCreatedBook;

    private static final String errorNotFound = "Not Found";

    @BeforeEach
    void setUp() {
        bookHandler = new BookHandler();
        booksToCleanUp = new ArrayList<>();
        preCreatedBook = DataFactory.createDefaultBook();
        Response response = bookHandler.createBook(preCreatedBook); // need to have at least 1 book before each test
        assertCode(response.getStatusCode(), 200);

        saveBookIdForCleanUp(response); // need to handle added items, in order to clean data after tests
    }

    @AfterEach
    void tearDown() {
        for (String id : booksToCleanUp) {
            try {
                bookHandler.deleteBook(id);
            } catch (Exception e) {
                logAllureStep("Couldn't delete book with following ID - " + id + ". Reason: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Test DELETE request for the deleted book")
    void shouldReturn404ForDeletedBookDelete() {
        Response response = bookHandler.deleteBook(preCreatedBook.getId().toString());
        assertCode(response.getStatusCode(), 200);

        Response responseWithError = bookHandler.deleteBook(preCreatedBook.getId().toString());
        assertCode(responseWithError.getStatusCode(), 404);

        logAllureStep("Verify that book couldn't be deleted by ID, that already deleted");
        assertThat("Invalid error message was received",
                responseWithError.getBody().jsonPath().getString("title"),
                Matchers.equalTo(errorNotFound));
    }

    @Test
    @DisplayName("Test GET request for the deleted book")
    void getBookByIdSuccessfully() {
        Response response = bookHandler.deleteBook(preCreatedBook.getId().toString());
        assertCode(response.getStatusCode(), 200);

        Response responseWithError = bookHandler.getBookById(preCreatedBook.getId().toString());

        assertCode(responseWithError.getStatusCode(), 404);

        logAllureStep("Verify that book couldn't be get by ID, that already deleted");
        assertThat("Invalid error message was received",
                responseWithError.getBody().jsonPath().getString("title"),
                Matchers.equalTo(errorNotFound));
    }

    @Test
    @DisplayName("Test PUT request for the deleted book")
    void updateBookByIdSuccessfully() {
        Response response = bookHandler.deleteBook(preCreatedBook.getId().toString());
        assertCode(response.getStatusCode(), 200);

        preCreatedBook.setDescription(preCreatedBook.getDescription() + "_updated");
        Response responseWithError = bookHandler.updateBook(preCreatedBook);

        assertCode(responseWithError.getStatusCode(), 404);

        logAllureStep("Verify that book couldn't be update by ID, that already deleted");
        assertThat("Invalid error message was received",
                responseWithError.getBody().jsonPath().getString("title"),
                Matchers.equalTo(errorNotFound));
    }

    private void saveBookIdForCleanUp(Response response) {
        String newId = response.jsonPath().get("id").toString();
        if (newId != null) {
            booksToCleanUp.add(newId);
        }
    }
}

