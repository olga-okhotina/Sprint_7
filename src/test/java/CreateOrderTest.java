import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(AllureJunit5.class)
@DisplayName("Создание заказа")
public class CreateOrderTest {

    private int track;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @AfterEach
    public void tearDown() {
        if (track != 0) {
            given()
                    .header("Content-type", "application/json")
                    .body("{\"track\": " + track + "}")
                    .put("/api/v1/orders/cancel");
        }
    }

    public static Stream<Arguments> colorData() {
        return Stream.of(
                Arguments.of(List.of("BLACK"), "один цвет BLACK"),
                Arguments.of(List.of("GREY"), "один цвет GREY"),
                Arguments.of(List.of("BLACK", "GREY"), "оба цвета"),
                Arguments.of(List.of(), "без цвета")
        );
    }

    @ParameterizedTest(name = "Создание заказа: {1}")
    @MethodSource("colorData")
    @DisplayName("Создание заказа с разными цветами")
    public void createOrderWithDifferentColors(List<String> colors, String description) {
        Order order = new Order(
                "Naruto", "Uchiha", "Konoha, 142 apt.", 4,
                "+7 800 355 35 35", 5, "2026-06-06", "Test comment", colors);

        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/v1/orders");

        response.then()
                .statusCode(201)
                .body("track", notNullValue());

        track = response.then().extract().path("track");
    }
}
