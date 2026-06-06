
package tests;
import base.BaseTest;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

public class UserTest extends BaseTest {

    @Test
    public void getUsers() {
        when().get("/users").then().statusCode(200);
    }
}
