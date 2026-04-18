package praktikum;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.order.OrderClient;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Список заказов")
public class GetOrdersListTest {

    private final OrderClient client = new OrderClient();

    @Test
    @DisplayName("В тело ответа возвращается список заказов")
    public void getOrdersListReturnsOrders() {
        ValidatableResponse response = client.getAll();
        response.assertThat()
                .statusCode(HTTP_OK)
                .body("orders", notNullValue())
                .body("orders", hasSize(greaterThan(0)));
    }
}
