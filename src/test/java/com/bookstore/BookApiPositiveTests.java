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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

@Epic("Bookstore API")
@Feature("Books Management")
public class BookApiPositiveTests {

    private BookHandler bookHandler;
    private List<String> booksToCleanUp;
    private BookBody preCreatedBook;

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
    @DisplayName("Test GET request for books - getting all books")
    void getAllBooksSuccessfully() {
        Response response = bookHandler.getBooks();

        assertCode(response.getStatusCode(), 200);

        List<Object> books = response.jsonPath().getList("$");

        assertThat("Books list should not be null", books, is(notNullValue()));
        assertThat("Books list should not be empty", books, is(not(empty())));

        assertSchema("Invalid contract for the GET books response",
                response,
                PATH_SCHEMA_BOOKS_RESPONSE);
    }

    @Test
    @DisplayName("Test POST request for creating book")
    void createBooksSuccessfully() {
        BookBody expectedBook = DataFactory.createDefaultBook();
        Response response = bookHandler.createBook(expectedBook);

        assertCode(response.getStatusCode(), 200);
        BookBody actualBook = response.as(BookBody.class);
        saveBookIdForCleanUp(response);

        assertSchema("Invalid contract for the POST book response",
                response,
                PATH_SCHEMA_BOOK_RESPONSE);

        assertThat("Invalid book was created by POST books request",
                actualBook,
                Matchers.equalTo(expectedBook));
    }

    @Test
    @DisplayName("Test DELETE request for deleting book")
    void deleteBookSuccessfully() {
        Response response = bookHandler.deleteBook(preCreatedBook.getId().toString());

        assertCode(response.getStatusCode(), 200);

        assertThat("Invalid response after DELETE request",
                response.getBody().asString(),
                Matchers.equalTo("")); // verify that delete response include empty body
    }

    @Test
    @DisplayName("Test GET request for the book by ID")
    void getBookByIdSuccessfully() {
        // need to get all books, because book that I create within preconditions could not be found by ID
        // that was workaround for testing fake endpoint, but decide to leave code that is failing on the status verification
        /* Response responseAllBooks = bookHandler.getBooks();
        BookBody expectedBook = responseAllBooks.getBody().jsonPath()
                .getList(".", BookBody.class)
                .stream()
                .findAny()
                .orElseThrow(() -> new AssertionError("List of books is empty"));*/

        Response response = bookHandler.getBookById(preCreatedBook.getId().toString());

        assertCode(response.getStatusCode(), 200);
        BookBody actualBook = response.as(BookBody.class);

        assertSchema("Invalid contract for the GET book by ID response",
                response,
                PATH_SCHEMA_BOOK_RESPONSE);

        assertThat("Invalid book was received by GET books by ID request",
                actualBook,
                Matchers.equalTo(preCreatedBook));
    }

    @Test
    @DisplayName("Test PUT request for the book by ID")
    void updateBookByIdSuccessfully() {
        preCreatedBook.setDescription(preCreatedBook.getDescription() + "_updated");
        Response response = bookHandler.updateBook(preCreatedBook);

        assertCode(response.getStatusCode(), 200);
        BookBody actualBook = response.as(BookBody.class);

        assertSchema("Invalid contract for the PUT book by ID response",
                response,
                PATH_SCHEMA_BOOK_RESPONSE);

        assertThat("Invalid book was received by PUT books by ID request",
                actualBook,
                Matchers.equalTo(preCreatedBook));
    }

    private void saveBookIdForCleanUp(Response response) {
        String newId = response.jsonPath().get("id").toString();
        if (newId != null) {
            booksToCleanUp.add(newId);
        }
    }
}

