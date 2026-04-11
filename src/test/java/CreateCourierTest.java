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

@ExtendWith(AllureJunit5.class)
@DisplayName("Создание курьера")
public class CreateCourierTest {

    private String login;
    private String password;
    private int courierId;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        login = "ninja" + System.currentTimeMillis();
        password = "1234";
    }

    @AfterEach
    public void tearDown() {
        if (courierId != 0) {
            given()
                    .header("Content-type", "application/json")
                    .delete("/api/v1/courier/" + courierId);
        }
    }

    @Test
    @DisplayName("Курьера можно создать")
    public void courierCanBeCreated() {
        Courier courier = new Courier(login, password, "saske");

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");

        response.then().statusCode(201);

        courierId = given()
                .header("Content-type", "application/json")
                .body(new CourierCredentials(login, password))
                .post("/api/v1/courier/login")
                .then().extract().path("id");
    }

    @Test
    @DisplayName("Успешный запрос возвращает ok: true")
    public void successResponseReturnsOkTrue() {
        Courier courier = new Courier(login, password, "saske");

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");

        response.then().body("ok", equalTo(true));

        courierId = given()
                .header("Content-type", "application/json")
                .body(new CourierCredentials(login, password))
                .post("/api/v1/courier/login")
                .then().extract().path("id");
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void cannotCreateDuplicateCourier() {
        Courier courier = new Courier(login, password, "saske");

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");

        courierId = given()
                .header("Content-type", "application/json")
                .body(new CourierCredentials(login, password))
                .post("/api/v1/courier/login")
                .then().extract().path("id");

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");

        response.then().statusCode(409);
    }

    @Test
    @DisplayName("Создание курьера с существующим логином возвращает ошибку")
    public void duplicateLoginReturnsError() {
        Courier courier = new Courier(login, password, "saske");

        given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");

        courierId = given()
                .header("Content-type", "application/json")
                .body(new CourierCredentials(login, password))
                .post("/api/v1/courier/login")
                .then().extract().path("id");

        Courier duplicate = new Courier(login, "other", "other");

        Response response = given()
                .header("Content-type", "application/json")
                .body(duplicate)
                .post("/api/v1/courier");

        response.then().statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без логина возвращает ошибку")
    public void createWithoutLoginReturnsError() {
        Courier courier = new Courier(null, "1234", "saske");

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");

        response.then().statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без пароля возвращает ошибку")
    public void createWithoutPasswordReturnsError() {
        Courier courier = new Courier(login, null, "saske");

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");

        response.then().statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}
