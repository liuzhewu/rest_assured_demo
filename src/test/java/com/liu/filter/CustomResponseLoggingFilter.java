package com.liu.filter;

import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseBuilder;
import io.restassured.filter.FilterContext;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.internal.print.ResponsePrinter;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.commons.lang3.Validate;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CustomResponseLoggingFilter extends ResponseLoggingFilter {
    private final PrintStream stream;
    private final Matcher<?> matcher;
    private final LogDetail logDetail;
    private final boolean shouldPrettyPrint;
    private final Set<String> blacklistedHeaders;

    public CustomResponseLoggingFilter() {
        this(LogDetail.ALL, isPrettyPrintingEnabled(), System.out, Matchers.any(Integer.class), Collections.emptySet());
    }

    public CustomResponseLoggingFilter(LogDetail logDetail, boolean prettyPrint, PrintStream stream, Matcher<? super Integer> matcher, Set<String> blacklistedHeaders) {
        Validate.notNull(logDetail, "Log details cannot be null", new Object[0]);
        Validate.notNull(stream, "Print stream cannot be null", new Object[0]);
        Validate.notNull(matcher, "Matcher cannot be null", new Object[0]);
        Validate.notNull(blacklistedHeaders, "Blacklisted headers cannot be null", new Object[0]);
        if (logDetail != LogDetail.PARAMS && logDetail != LogDetail.URI && logDetail != LogDetail.METHOD) {
            this.shouldPrettyPrint = prettyPrint;
            this.logDetail = logDetail;
            this.stream = stream;
            this.matcher = matcher;
            this.blacklistedHeaders = new HashSet(blacklistedHeaders);
        } else {
            throw new IllegalArgumentException(String.format("%s is not a valid %s for a response.", logDetail, LogDetail.class.getSimpleName()));
        }
    }

    private static boolean isPrettyPrintingEnabled() {
        return RestAssured.config == null || RestAssured.config.getLogConfig().isPrettyPrintingEnabled();
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);
        int statusCode = response.statusCode();
        if (this.matcher.matches(statusCode)) {
            String responseStr = ResponsePrinter.print(response, response, this.stream, this.logDetail, this.shouldPrettyPrint, this.blacklistedHeaders);
            Allure.addAttachment("response", responseStr);
            byte[] responseBody;
            if (this.logDetail != LogDetail.BODY && this.logDetail != LogDetail.ALL) {
                responseBody = null;
            } else {
                responseBody = response.asByteArray();
            }

            response = this.cloneResponseIfNeeded(response, responseBody);
        }

        return response;
    }

    private Response cloneResponseIfNeeded(Response response, byte[] responseAsString) {
        if (responseAsString != null && response instanceof RestAssuredResponseImpl && !((RestAssuredResponseImpl) response).getHasExpectations()) {
            Response build = (new ResponseBuilder()).clone(response).setBody(responseAsString).build();
            ((RestAssuredResponseImpl) build).setHasExpectations(true);
            return build;
        } else {
            return response;
        }
    }

}
