package com.bookstore.handlers;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;

public class BaseAbstractHandler {

    protected RequestSpecification requestSpecification;

    public BaseAbstractHandler() {
        requestSpecification = new RequestSpecBuilder()
                .addHeader("content-type", "application/json;charset=UTF-8")
                // may add token here, if we need in future
                .addHeader("authorization", "Bearer ")
                .setBaseUri("https://fakerestapi.azurewebsites.net/api/v1/") // need to put this into properties file
                .log(LogDetail.ALL)
                .addFilter(new AllureRestAssured())
                .build();
    }
}
