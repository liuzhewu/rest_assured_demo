package com.liu.test;

import com.liu.pojo.Member;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.liu.constant.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.module.jsv.JsonSchemaValidator.*;

public class PostTest extends BaseTest {

    @Test
    public void testPost01() {
//        post请求，参数通过form表单
        given().
                contentType("application/x-www-form-urlencoded; charset=UTF-8").
                formParam("name", "张三").
                when().
                post(URL + "/post").
                then().
                log().body();
    }

    @Test
    public void testPost02() {
//        post请求，参数通过json类型
        String jsonStr = "{\"name\":\"张三\"}";
        given().
                contentType(ContentType.JSON).
                body(jsonStr).
                when().
                post(URL + "/post").
                then().
                log().body();
    }

    @Test
    public void testPost03() {
//        post请求，参数通过multiPart-文件上传操作
        given().
                multiPart(new File(RESOURCES + SP + "attachment" + SP + "2.png")).
                when().
                post(URL + "/post").
                then().
                log().body();
    }

    @Test
    public void testPost04() {
//        post请求，参数通过xml
        String xmlStr = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                "<suite>\n" +
                "    <class>测试xml</class>\n" +
                "</suite>";
        given().
                contentType(ContentType.XML).
                body(xmlStr).
                when().
                post(URL + "/post").
                then().
                log().body();
    }

    @Test
    public void objectToJson() {
//        java对象转json
        Member member = new Member("张三", 15);
        given().
                contentType(ContentType.JSON).
                body(member).
                when().
                post(URL + "/post").
                then().
                log().body();
    }

    @Test
    public void hashMapToJson() {
//        hashMap转json
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", 15);
        given().
                contentType(ContentType.JSON).
                body(map).
                when().
                post(URL + "/post").
                then().
                log().body();
    }

    @Test
    public void assertTest() {
//        hashMap转json
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", 15);
        given().
                contentType(ContentType.JSON).
                body(map).
                when().
                post(URL + "/post").
                then().
//                响应的状态码
//                assertThat().statusCode(300);
//        响应的头部断言
        assertThat().header("Content-Type", "application/json");
//                log().all();

//        testng提供的断言机制
//        Assert.assertEquals("hello","world");
//        Assert.assertTrue(10>9);

    }

    /**
     * postman配置的json（可找rest-assured在github官方的usage guide）:
     * {
     * "lotto":{
     * "lottoId":5,
     * "winning-numbers":[2,45,34,23,7,5,3],
     * "winners":[{
     * "winnerId":23,
     * "numbers":[2,45,34,23,3,5]
     * },{
     * "winnerId":54,
     * "numbers":[52,3,12,11,18,22]
     * }]
     * }
     * }
     */
    @Test
    public void testMockJson() {
//向postman mockserver发起请求，响应数据为json
        given().
                when().
                get(MOCK_URL + "/json").
                then().
                log().all();
    }

    /**
     * postman配置的xml：
     * <shopping>
     * <category type="groceries">
     * <item>Chocolate</item>
     * <item>Coffee</item>
     * </category>
     * <category type="supplies">
     * <item>Paper</item>
     * <item quantity="4">Pens</item>
     * </category>
     * <category type="present">
     * <item when="Aug 10">Kathryn's Birthday</item>
     * </category>
     * </shopping>
     */
    @Test
    public void testMockXml() {
//向postman mockserver发起请求，响应数据为xml
        given().
                when().
                get(MOCK_URL + "/xml").
                then().
                log().all();
    }

    @Test
    public void assertJson() {
//json断言,用gpath语法
        given().
                when().
                get(MOCK_URL + "/json").
                then().
//                assertThat().body("lotto.lottoId",equalTo(5));
//        包含所有符合条件的
//                assertThat().body("lotto.winners.winnerId",hasItems(23,54));
//        通过索引访问
//                assertThat().body("lotto.winners.winnerId[0]",equalTo(23));
//                assertThat().body("lotto.winners.winnerId[0]",greaterThan(20));
//        gpath复杂语法,
//         lotto.winners下的winnerId大于40的第一个元素值为54。findAll中的 it表示前面的集合
//                assertThat().body("lotto.winners.findAll{it.winnerId>40}.winnerId[0]",equalTo(54));
//        find只找符合条件的第一项
//                assertThat().body("lotto.winners.find{it.winnerId>40}.winnerId",equalTo(54));
//        sum：求和
//                assertThat().body("lotto.winning-numbers.sum()",equalTo(119));
//                assertThat().body("lotto.winners.findAll{it.winnerId>20}.winnerId.sum()",equalTo(77));
                //max,min
//                assertThat().body("lotto.winning-numbers.max()",equalTo(45));
        assertThat().body("lotto.winning-numbers.min()", equalTo(2));


    }

    @Test
    public void assertJsonSchema() {
//json schema断言
        given().
                when().
                get(MOCK_URL + "/json").
                then().
//    默认是从resources目录下读取
        assertThat().body(matchesJsonSchemaInClasspath("schema"+SP+"get.json"));


    }


}
