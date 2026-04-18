package praktikum.courier;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.Client;
import praktikum.Credentials;

import java.util.Map;

public class CourierClient extends Client {
    @Step("логин курьера")
    public ValidatableResponse logIn(Credentials creds) {
        return spec()
                .body(creds)
                .when()
                .post("/courier/login")
                .then().log().all();
    }

    @Step("логин курьера с неполными данными")
    public ValidatableResponse logIn(Map<String, String> creds) {
        return spec()
                .body(creds)
                .when()
                .post("/courier/login")
                .then().log().all();
    }

    @Step("создать курьера")
    public ValidatableResponse create(Courier courier) {
        return spec()
                .body(courier)
                .when()
                .post("/courier")
                .then().log().all();
    }

    @Step("удалить курьера")
    public ValidatableResponse deleteCourier(int id) {
        return spec()
                .when()
                .delete("/courier/" + id)
                .then().log().all();
    }
}
