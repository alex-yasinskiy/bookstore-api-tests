package com.bookstore.utils;

import com.bookstore.models.AuthorBody;
import com.bookstore.models.BookBody;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;
import java.time.temporal.ChronoUnit;

public class DataFactory {

    public static BookBody createDefaultBook() {
        return BookBody.builder()
                .id(ThreadLocalRandom.current().nextInt(1, 100000))
                .title("automation-book-title-" + generateRandomString())
                .description("automation-book-description-" + generateRandomString())
                .pageCount(ThreadLocalRandom.current().nextInt(50, 1000))
                .excerpt("automation-book-excerpt-" + generateRandomString())
                .publishDate(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .build();
    }

    public static AuthorBody createDefaultAuthor() {
        return AuthorBody.builder()
                .id(ThreadLocalRandom.current().nextInt(1, 100000))
                .idBook(ThreadLocalRandom.current().nextInt(1, 100000))
                .firstName("automation-author-firstname-" + generateRandomString())
                .lastName("automation-author-lastname-" + generateRandomString())
                .build();
    }

    private static String generateRandomString() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
