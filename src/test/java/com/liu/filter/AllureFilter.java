package com.liu.filter;

import io.qameta.allure.Step;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.internal.NoParameterValue;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.ProxySpecification;
import org.apache.commons.lang3.SystemUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AllureFilter implements Filter {

    private static void addCookies(FilterableRequestSpecification requestSpec, StringBuilder builder) {
        builder.append("Cookies:");
        Cookies cookies = requestSpec.getCookies();
        if (!cookies.exist()) {
            appendTwoTabs(builder).append("<none>").append(SystemUtils.LINE_SEPARATOR);
        }

        int i = 0;

        Cookie cookie;
        for (Iterator var4 = cookies.iterator(); var4.hasNext(); builder.append(cookie).append(SystemUtils.LINE_SEPARATOR)) {
            cookie = (Cookie) var4.next();
            if (i++ == 0) {
                appendTwoTabs(builder);
            } else {
                appendFourTabs(builder);
            }
        }

    }

    private static void addHeaders(FilterableRequestSpecification requestSpec, Set<String> blacklistedHeaders, StringBuilder builder) {
        builder.append("Headers:");
        Headers headers = requestSpec.getHeaders();
        if (!headers.exist()) {
            appendTwoTabs(builder).append("<none>").append(SystemUtils.LINE_SEPARATOR);
        } else {
            int i = 0;

            Header processedHeader;
            for (Iterator var5 = headers.iterator(); var5.hasNext(); builder.append(processedHeader).append(SystemUtils.LINE_SEPARATOR)) {
                Header header = (Header) var5.next();
                if (i++ == 0) {
                    appendTwoTabs(builder);
                } else {
                    appendFourTabs(builder);
                }

                processedHeader = header;
                if (blacklistedHeaders.contains(header.getName())) {
                    processedHeader = new Header(header.getName(), "[ BLACKLISTED ]");
                }
            }
        }

    }

    private static void addMapDetails(StringBuilder builder, String title, Map<String, ?> map) {
        appendTab(builder.append(title));
        if (map.isEmpty()) {
            builder.append("<none>").append(SystemUtils.LINE_SEPARATOR);
        } else {
            int i = 0;

            for (Iterator var4 = map.entrySet().iterator(); var4.hasNext(); builder.append(SystemUtils.LINE_SEPARATOR)) {
                Map.Entry<String, ?> entry = (Map.Entry) var4.next();
                if (i++ != 0) {
                    appendFourTabs(builder);
                }

                Object value = entry.getValue();
                builder.append((String) entry.getKey());
                if (!(value instanceof NoParameterValue)) {
                    builder.append("=").append(value);
                }
            }
        }
    }

    private static StringBuilder appendFourTabs(StringBuilder builder) {
        appendTwoTabs(appendTwoTabs(builder));
        return builder;
    }

    private static void addProxy(FilterableRequestSpecification requestSpec, StringBuilder builder) {
        builder.append("Proxy:");
        ProxySpecification proxySpec = requestSpec.getProxySpecification();
        appendThreeTabs(builder);
        if (proxySpec == null) {
            builder.append("<none>");
        } else {
            builder.append(proxySpec.toString());
        }

        builder.append(SystemUtils.LINE_SEPARATOR);
    }

    private static void addSingle(StringBuilder builder, String str, String requestPath) {
        appendTab(builder.append(str)).append(requestPath).append(SystemUtils.LINE_SEPARATOR);
    }

    private static StringBuilder appendTab(StringBuilder builder) {
        return builder.append("\t");
    }

    private static StringBuilder appendThreeTabs(StringBuilder builder) {
        appendTwoTabs(appendTab(builder));
        return builder;
    }

    private static StringBuilder appendTwoTabs(StringBuilder builder) {
        appendTab(appendTab(builder));
        return builder;
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);
        StringBuilder builder = new StringBuilder();
//        builder.append("<h3>request</h3>");
        builder.append("Request method:\t" + requestSpec.getMethod() + '\n');
        builder.append("Request URI:\t" + requestSpec.getURI() + '\n');
        builder.append("Request params:\t" + requestSpec.getRequestParams() + '\n');
        builder.append("Query params:\t" + requestSpec.getQueryParams() + '\n');
        builder.append("Form params:\t" + requestSpec.getFormParams() + '\n');
        builder.append("Path params:\t" + requestSpec.getPathParams() + '\n');
        builder.append("Cookies:\t\t" + requestSpec.getCookies() + '\n');
        builder.append("Body:\t\t\t:" + requestSpec.getBody() + '\n');
//        LogUtils.info(context.toString());
        requestBody(requestSpec.getURI(), builder.toString());

        addSingle(builder, "Request method:", requestSpec.getMethod());
        addSingle(builder, "Request URI:", requestSpec.getURI());
        addProxy(requestSpec, builder);
        addMapDetails(builder, "Request params:", requestSpec.getRequestParams());
        addMapDetails(builder, "Query params:", requestSpec.getQueryParams());
        addMapDetails(builder, "Form params:", requestSpec.getFormParams());
        addMapDetails(builder, "Path params:", requestSpec.getNamedPathParams());
        addHeaders(requestSpec, new HashSet<>(), builder);
        addCookies(requestSpec, builder);


//        Allure.step(context.toString());

        StringBuilder responseContext = new StringBuilder();
//        responseContext.append("<h3>response</h3>");
        responseContext.append(response.getStatusLine() + '\n');
        responseContext.append(response.headers().toString() + '\n');
        responseContext.append(response.getBody().asString() + '\n');

//        LogUtils.info(context.toString());

//        Allure.step(context.toString());
        respondBody(responseContext.toString());


        return response;
    }

    @Step
    public void requestBody(String URL, String Body) {
        //报告展现请求报文
    }

    @Step
    public void respondBody(String Respond) {
        //报告展现响应报文
    }
}
