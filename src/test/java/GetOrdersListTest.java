import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(AllureJunit5.class)
@DisplayName("Список заказов")
public class GetOrdersListTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("В тело ответа возвращается список заказов")
    public void getOrdersListReturnsOrders() {
        Response response = given()
                .header("Content-type", "application/json")
                .get("/api/v1/orders");

        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}
