package praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import praktikum.order.Order;
import praktikum.order.OrderClient;

import java.util.List;
import java.util.stream.Stream;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Создание заказа")
public class CreateOrderTest {

    private final OrderClient client = new OrderClient();
    private int track;

    @AfterEach
    public void cancelOrder() {
        if (track != 0) {
            client.cancel(track);
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

        ValidatableResponse response = client.create(order);
        response.assertThat()
                .statusCode(HTTP_CREATED)
                .body("track", notNullValue());

        track = response.extract().path("track");
    }
}
