package praktikum.courier;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.*;

public class CourierChecker {
    @Step("логин успешный")
    public Integer loggedInSuccessfully(ValidatableResponse loginResponse) {
        int id = loginResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .path("id");

        assertNotEquals(0, id);

        return id;
    }

    @Step("курьера удалось создать")
    public void createdSuccessfully(ValidatableResponse createResponse) {
        boolean created = createResponse
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_CREATED)
                .extract()
                .path("ok");
        assertTrue(created);
    }

    @Step("создание курьера завершилось ошибкой")
    public String creationFailed(ValidatableResponse response, int statusCode) {
        return response
                .assertThat()
                .statusCode(statusCode)
                .extract()
                .path("message");
    }

    @Step("логин завершился ошибкой")
    public String loginFailed(ValidatableResponse response, int statusCode) {
        return response
                .assertThat()
                .statusCode(statusCode)
                .extract()
                .path("message");
    }

    @Step("курьер удалён")
    public void deletedSuccessfully(ValidatableResponse response) {
        boolean ok = response
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .extract()
                .path("ok");
        assertTrue(ok);
    }
}
