package com.liu.test;

import org.testng.annotations.Test;

import static com.liu.constant.Constants.URL;
import static io.restassured.RestAssured.given;

public class GetTest extends BaseTest {

    @Test
    public void testGet01() {
//        get请求，参数通过queryParam添加
        given().
                queryParam("name", "jack").
                when().
                get(URL + "/get").
                then();
    }

    @Test
    public void testGet02() {
//        get请求，参数通过url添加
        given().
                when().
                get(URL + "/get?name=mike").
                then().
                log().body();
    }

    @Test
    public void testGet03() {
//        测试发送2个请求，allure报告如何显示
        given().
                queryParam("name", "jack").
                when().
                get(URL + "/get").
                then();

        given().
                when().
                get(URL + "/get?name=mike").
                then().
                log().body();
    }


}
