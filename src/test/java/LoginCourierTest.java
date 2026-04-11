import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(AllureJunit5.class)
@DisplayName("Логин курьера")
public class LoginCourierTest {

    private String login;
    private String password;
    private int courierId;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        login = "ninja" + System.currentTimeMillis();
        password = "1234";

        Courier courier = new Courier(login, password, "saske");
        given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");
    }

    @AfterEach
    public void tearDown() {
        if (courierId == 0) {
            try {
                courierId = given()
                        .header("Content-type", "application/json")
                        .body(new CourierCredentials(login, password))
                        .post("/api/v1/courier/login")
                        .then().extract().path("id");
            } catch (Exception ignored) {
            }
        }
        if (courierId != 0) {
            given()
                    .header("Content-type", "application/json")
                    .delete("/api/v1/courier/" + courierId);
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    public void courierCanLogin() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new CourierCredentials(login, password))
                .post("/api/v1/courier/login");

        response.then().statusCode(200);
        courierId = response.then().extract().path("id");
    }

    @Test
    @DisplayName("Успешный запрос возвращает id")
    public void successLoginReturnsId() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new CourierCredentials(login, password))
                .post("/api/v1/courier/login");

        response.then().statusCode(200).body("id", notNullValue());
        courierId = response.then().extract().path("id");
    }

    @Test
    @DisplayName("Логин без поля login возвращает ошибку")
    public void loginWithoutLoginFieldReturnsError() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new CourierCredentials(null, password))
                .post("/api/v1/courier/login");

        response.then().statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин без поля password возвращает ошибку")
    public void loginWithoutPasswordFieldReturnsError() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new CourierCredentials(login, null))
                .post("/api/v1/courier/login");

        response.then().statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин с неправильным логином возвращает ошибку")
    public void loginWithWrongLoginReturnsError() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new CourierCredentials("wrongLogin123", password))
                .post("/api/v1/courier/login");

        response.then().statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин с неправильным паролем возвращает ошибку")
    public void loginWithWrongPasswordReturnsError() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new CourierCredentials(login, "wrongPass"))
                .post("/api/v1/courier/login");

        response.then().statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация под несуществующим пользователем возвращает ошибку")
    public void loginNonExistentCourierReturnsError() {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new CourierCredentials("nonExistent999", "noPass"))
                .post("/api/v1/courier/login");

        response.then().statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
