package com.bookstore;

import com.bookstore.handlers.AuthorHandler;
import com.bookstore.handlers.BookHandler;
import com.bookstore.models.AuthorBody;
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

import static com.bookstore.handlers.AuthorHandler.PATH_SCHEMA_AUTHORS_RESPONSE;
import static com.bookstore.handlers.AuthorHandler.PATH_SCHEMA_AUTHOR_RESPONSE;
import static com.bookstore.utils.CustomAsserts.assertCode;
import static com.bookstore.utils.CustomAsserts.assertSchema;
import static com.bookstore.utils.Utils.logAllureStep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.empty;

@Epic("Bookstore API")
@Feature("Authors Management - positive tests")
public class AuthorApiPositiveTests {

    private AuthorHandler authorHandler;
    private BookHandler bookHandler;
    private List<String> authorsToCleanUp;
    private List<String> booksToCleanUp;
    private AuthorBody preCreatedAuthor;
    private BookBody preCreatedBook;

    @BeforeEach
    void setUp() {
        bookHandler = new BookHandler();
        authorHandler = new AuthorHandler();
        authorsToCleanUp = new ArrayList<>();
        booksToCleanUp = new ArrayList<>();
        preCreatedBook = DataFactory.createDefaultBook();
        Response responseBook = bookHandler.createBook(preCreatedBook);
        assertCode(responseBook.getStatusCode(), 200);
        preCreatedAuthor = DataFactory.createDefaultAuthor();
        preCreatedAuthor.setIdBook(preCreatedBook.getId());
        Response response = authorHandler.createAuthor(preCreatedAuthor); // need to have at least 1 author before each test
        assertCode(response.getStatusCode(), 200);

        saveAuthorIdForCleanUp(response); // need to handle added items, in order to clean data after tests
        saveBookIdForCleanUp(response);
    }

    @AfterEach
    void tearDown() {
        for (String id : authorsToCleanUp) {
            try {
                authorHandler.deleteAuthor(id);
            } catch (Exception e) {
                logAllureStep("Couldn't delete author with following ID - " + id + ". Reason: " + e.getMessage());
            }
        }

        for (String id : booksToCleanUp) {
            try {
                bookHandler.deleteBook(id);
            } catch (Exception e) {
                logAllureStep("Couldn't delete book with following ID - " + id + ". Reason: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Test GET request for authors - getting all authors")
    void getAllAuthorsSuccessfully() {
        Response response = authorHandler.getAuthors();

        assertCode(response.getStatusCode(), 200);

        List<Object> authors = response.jsonPath().getList("$");

        assertThat("Authors list should not be null", authors, is(notNullValue()));
        assertThat("Authors list should not be empty", authors, is(not(empty())));

        assertSchema("Invalid contract for the GET authors response",
                response,
                PATH_SCHEMA_AUTHORS_RESPONSE);
    }

    @Test
    @DisplayName("Test POST request for creating author")
    void createBooksSuccessfully() {
        Response responseBook = bookHandler.createBook(DataFactory.createDefaultBook());
        assertCode(responseBook.getStatusCode(), 200);
        saveBookIdForCleanUp(responseBook);

        AuthorBody expectedAuthor = DataFactory.createDefaultAuthor();
        expectedAuthor.setIdBook(responseBook.getBody().jsonPath().getInt("id"));
        Response response = authorHandler.createAuthor(expectedAuthor);

        assertCode(response.getStatusCode(), 200);

        AuthorBody actualAuthor = response.as(AuthorBody.class);
        saveAuthorIdForCleanUp(response);

        assertSchema("Invalid contract for the POST author response",
                response,
                PATH_SCHEMA_AUTHOR_RESPONSE);

        assertThat("Invalid author was created by POST author request",
                actualAuthor,
                Matchers.equalTo(expectedAuthor));
    }

    @Test
    @DisplayName("Test DELETE request for deleting author")
    void deleteAuthorSuccessfully() {
        Response response = authorHandler.deleteAuthor(preCreatedAuthor.getId().toString());

        assertCode(response.getStatusCode(), 200);

        assertThat("Invalid response after DELETE request",
                response.getBody().asString(),
                Matchers.equalTo("")); // verify that delete response include empty body
    }

    @Test
    @DisplayName("Test GET request for the author by ID")
    void getAuthorByIdSuccessfully() {
        Response response = authorHandler.getAuthorById(preCreatedAuthor.getId().toString());

        assertCode(response.getStatusCode(), 200);
        AuthorBody actualAuthor = response.as(AuthorBody.class);

        assertSchema("Invalid contract for the GET author by ID response",
                response,
                PATH_SCHEMA_AUTHOR_RESPONSE);

        assertThat("Invalid author was received by GET author by ID request",
                actualAuthor,
                Matchers.equalTo(preCreatedAuthor));
    }

    @Test
    @DisplayName("Test PUT request for the author by ID")
    void updateAuthorByIdSuccessfully() {
        preCreatedAuthor.setFirstName(preCreatedAuthor.getFirstName() + "_updated");
        Response response = authorHandler.updateAuthor(preCreatedAuthor);

        assertCode(response.getStatusCode(), 200);
        AuthorBody actualBook = response.as(AuthorBody.class);

        assertSchema("Invalid contract for the PUT author by ID response",
                response,
                PATH_SCHEMA_AUTHOR_RESPONSE);

        assertThat("Invalid author was received by PUT author by ID request",
                actualBook,
                Matchers.equalTo(preCreatedAuthor));
    }

    private void saveAuthorIdForCleanUp(Response response) {
        String newId = response.jsonPath().get("id").toString();
        if (newId != null) {
            authorsToCleanUp.add(newId);
        }
    }

    private void saveBookIdForCleanUp(Response response) {
        String newId = response.jsonPath().get("id").toString();
        if (newId != null) {
            booksToCleanUp.add(newId);
        }
    }

}
