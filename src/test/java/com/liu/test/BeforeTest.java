package com.liu.test;

import com.liu.filter.CustomRequestLoggingFilter;
import com.liu.filter.CustomResponseLoggingFilter;
import io.restassured.RestAssured;
import org.testng.annotations.Test;

/**
 * 登录，设置sessionId
 */
public class BeforeTest extends BaseTest {

    @Test
    public void before() {
        RestAssured.filters(new CustomRequestLoggingFilter(), new CustomResponseLoggingFilter());
    }


}
