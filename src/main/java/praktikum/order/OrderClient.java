package praktikum.order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.Client;

import java.util.Map;

public class OrderClient extends Client {
    @Step("создать заказ")
    public ValidatableResponse create(Order order) {
        return spec()
                .body(order)
                .when()
                .post("/orders")
                .then().log().all();
    }

    @Step("получить список заказов")
    public ValidatableResponse getAll() {
        return spec()
                .when()
                .get("/orders")
                .then().log().all();
    }

    @Step("отменить заказ")
    public ValidatableResponse cancel(int track) {
        return spec()
                .body(Map.of("track", track))
                .when()
                .put("/orders/cancel")
                .then().log().all();
    }
}
