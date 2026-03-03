package com.bookstore.handlers;

import com.bookstore.utils.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;

public abstract class BaseAbstractHandler {

    protected RequestSpecification requestSpecification;

    public BaseAbstractHandler() {
        requestSpecification = new RequestSpecBuilder()
                .addHeader("content-type", "application/json;charset=UTF-8")
                // may add token here, if we need in future
                .addHeader("authorization", "Bearer ")
                .setBaseUri(ConfigReader.getBaseUri())
                .log(LogDetail.ALL)
                .addFilter(new AllureRestAssured())
                .build();
    }
}
