package praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.courier.Courier;
import praktikum.courier.CourierChecker;
import praktikum.courier.CourierClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Логин курьера")
public class LoginCourierTest {

    private final CourierClient client = new CourierClient();
    private final CourierChecker check = new CourierChecker();

    private Courier courier;
    private int courierId;

    @BeforeEach
    public void setUp() {
        courier = Courier.random();
        client.create(courier);
    }

    @AfterEach
    public void dropCourier() {
        if (courierId == 0) {
            try {
                var creds = Credentials.fromCourier(courier);
                ValidatableResponse loginResponse = client.logIn(creds);
                courierId = loginResponse.extract().path("id");
            } catch (Exception ignored) {
            }
        }
        if (courierId != 0) {
            client.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    public void courierCanLogin() {
        var creds = Credentials.fromCourier(courier);
        ValidatableResponse loginResponse = client.logIn(creds);
        courierId = check.loggedInSuccessfully(loginResponse);
    }

    @Test
    @DisplayName("Успешный запрос возвращает id")
    public void successLoginReturnsId() {
        var creds = Credentials.fromCourier(courier);
        ValidatableResponse loginResponse = client.logIn(creds);
        courierId = check.loggedInSuccessfully(loginResponse);
        assertTrue(courierId > 0);
    }

    @Test
    @DisplayName("Логин без поля login возвращает ошибку")
    public void loginWithoutLoginFieldReturnsError() {
        ValidatableResponse response = client.logIn(Map.of("password", courier.getPassword()));
        String message = check.loginFailed(response, 400);
        assertEquals("Недостаточно данных для входа", message);
    }

    @Test
    @DisplayName("Логин без поля password возвращает ошибку")
    public void loginWithoutPasswordFieldReturnsError() {
        ValidatableResponse response = client.logIn(Map.of("login", courier.getLogin()));
        String message = check.loginFailed(response, 400);
        assertEquals("Недостаточно данных для входа", message);
    }

    @Test
    @DisplayName("Логин с неправильным логином возвращает ошибку")
    public void loginWithWrongLoginReturnsError() {
        var creds = new Credentials("wrongLogin123", courier.getPassword());
        ValidatableResponse response = client.logIn(creds);
        String message = check.loginFailed(response, 404);
        assertEquals("Учетная запись не найдена", message);
    }

    @Test
    @DisplayName("Логин с неправильным паролем возвращает ошибку")
    public void loginWithWrongPasswordReturnsError() {
        var creds = new Credentials(courier.getLogin(), "wrongPass");
        ValidatableResponse response = client.logIn(creds);
        String message = check.loginFailed(response, 404);
        assertEquals("Учетная запись не найдена", message);
    }

    @Test
    @DisplayName("Авторизация под несуществующим пользователем возвращает ошибку")
    public void loginNonExistentCourierReturnsError() {
        var creds = new Credentials("nonExistent999", "noPass");
        ValidatableResponse response = client.logIn(creds);
        String message = check.loginFailed(response, 404);
        assertEquals("Учетная запись не найдена", message);
    }
}
