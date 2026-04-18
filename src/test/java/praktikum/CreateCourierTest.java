package praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.courier.Courier;
import praktikum.courier.CourierChecker;
import praktikum.courier.CourierClient;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Создание курьера")
public class CreateCourierTest {

    private final CourierClient client = new CourierClient();
    private final CourierChecker check = new CourierChecker();

    private int courierId;

    @AfterEach
    public void dropCourier() {
        if (courierId != 0) {
            client.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Курьера можно создать")
    public void courierCanBeCreated() {
        var courier = Courier.random();
        ValidatableResponse createResponse = client.create(courier);
        check.createdSuccessfully(createResponse);

        var creds = Credentials.fromCourier(courier);
        ValidatableResponse loginResponse = client.logIn(creds);
        courierId = check.loggedInSuccessfully(loginResponse);
    }

    @Test
    @DisplayName("Успешный запрос возвращает ok: true")
    public void successResponseReturnsOkTrue() {
        var courier = Courier.random();
        ValidatableResponse createResponse = client.create(courier);
        check.createdSuccessfully(createResponse);

        var creds = Credentials.fromCourier(courier);
        ValidatableResponse loginResponse = client.logIn(creds);
        courierId = check.loggedInSuccessfully(loginResponse);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void cannotCreateDuplicateCourier() {
        var courier = Courier.random();
        ValidatableResponse createResponse = client.create(courier);
        check.createdSuccessfully(createResponse);

        var creds = Credentials.fromCourier(courier);
        ValidatableResponse loginResponse = client.logIn(creds);
        courierId = check.loggedInSuccessfully(loginResponse);

        ValidatableResponse duplicateResponse = client.create(courier);
        String message = check.creationFailed(duplicateResponse, 409);
        assertNotNull(message);
    }

    @Test
    @DisplayName("Создание курьера с существующим логином возвращает ошибку")
    public void duplicateLoginReturnsError() {
        var courier = Courier.random();
        ValidatableResponse createResponse = client.create(courier);
        check.createdSuccessfully(createResponse);

        var creds = Credentials.fromCourier(courier);
        ValidatableResponse loginResponse = client.logIn(creds);
        courierId = check.loggedInSuccessfully(loginResponse);

        var duplicate = new Courier(courier.getLogin(), "other", "other");
        ValidatableResponse duplicateResponse = client.create(duplicate);
        String message = check.creationFailed(duplicateResponse, 409);
        assertEquals("Этот логин уже используется. Попробуйте другой.", message);
    }

    @Test
    @DisplayName("Создание курьера без логина возвращает ошибку")
    public void createWithoutLoginReturnsError() {
        var courier = new Courier(null, "1234", "saske");

        ValidatableResponse response = client.create(courier);
        String message = check.creationFailed(response, 400);
        assertEquals("Недостаточно данных для создания учетной записи", message);
    }

    @Test
    @DisplayName("Создание курьера без пароля возвращает ошибку")
    public void createWithoutPasswordReturnsError() {
        var courier = new Courier("someLogin", null, "saske");

        ValidatableResponse response = client.create(courier);
        String message = check.creationFailed(response, 400);
        assertEquals("Недостаточно данных для создания учетной записи", message);
    }
}
