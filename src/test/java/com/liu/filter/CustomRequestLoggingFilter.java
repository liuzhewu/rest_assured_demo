package com.liu.filter;

import io.qameta.allure.Allure;
import io.restassured.filter.FilterContext;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.UrlDecoder;
import io.restassured.internal.print.RequestPrinter;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.commons.lang3.Validate;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CustomRequestLoggingFilter extends RequestLoggingFilter {
    private static final boolean SHOW_URL_ENCODED_URI = true;
    private final LogDetail logDetail;
    private final PrintStream stream;
    private final boolean shouldPrettyPrint;
    private final boolean showUrlEncodedUri;
    private final Set<String> blacklistedHeaders;
    private final Set<LogDetail> logDetailSet;

    public CustomRequestLoggingFilter() {
        this(LogDetail.ALL, true, System.out, true, Collections.emptySet());
    }

    public CustomRequestLoggingFilter(LogDetail logDetail, boolean shouldPrettyPrint, PrintStream stream, boolean showUrlEncodedUri, Set<String> blacklistedHeaders) {
        this.logDetailSet = new HashSet();
        Validate.notNull(stream, "Print stream cannot be null", new Object[0]);
        Validate.notNull(blacklistedHeaders, "Blacklisted headers cannot be null", new Object[0]);
        Validate.notNull(logDetail, "Log details cannot be null", new Object[0]);
        if (logDetail == LogDetail.STATUS) {
            throw new IllegalArgumentException(String.format("%s is not a valid %s for a request.", LogDetail.STATUS, LogDetail.class.getSimpleName()));
        } else {
            this.stream = stream;
            this.logDetail = logDetail;
            this.blacklistedHeaders = new HashSet(blacklistedHeaders);
            this.shouldPrettyPrint = shouldPrettyPrint;
            this.showUrlEncodedUri = showUrlEncodedUri;
        }

    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        String uri = requestSpec.getURI();
        if (!this.showUrlEncodedUri) {
            uri = UrlDecoder.urlDecode(uri, Charset.forName(requestSpec.getConfig().getEncoderConfig().defaultQueryParameterCharset()), true);
        }
        String requestStr;
        if (this.logDetailSet.isEmpty()) {
            requestStr = RequestPrinter.print(requestSpec, requestSpec.getMethod(), uri, this.logDetail, this.blacklistedHeaders, this.stream, this.shouldPrettyPrint);
        } else {
            requestStr = RequestPrinter.print(requestSpec, requestSpec.getMethod(), uri, this.logDetailSet, this.blacklistedHeaders, this.stream, this.shouldPrettyPrint);
        }
        Allure.addAttachment("request", requestStr);

        return ctx.next(requestSpec, responseSpec);
    }
}
