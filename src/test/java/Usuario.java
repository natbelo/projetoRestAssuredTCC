import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import com.github.javafaker.Faker;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Usuario {
    private static String userId;
    Faker faker = new Faker();
    String userName = faker.name().firstName();
    String userEmail = userName + "@qa.com.br";

    @BeforeClass
    public static void preCondition(){
        baseURI = "http://localhost";
        port = 3000;
    }
    @Test
    @Order(1)
    public void postUsuario(){

        Usuario.userId =
                given()
                        .body("{\n" +
                                "  \"nome\": \""+userName+"\",\n" +
                                "  \"email\": \""+userEmail+"\",\n" +
                                "  \"password\": \"teste\",\n" +
                                "  \"administrador\": \"true\"\n" +
                                "}")
                        .contentType(ContentType.JSON)
                .when()
                        .post("usuarios")
                .then()
                        .log().all()
                        .statusCode(HttpStatus.SC_CREATED)
                        .body("message", is("Cadastro realizado com sucesso"))
                        .extract().path("_id");

    }
    @Test
    @Order(2)
    public void getUsuarios(){
        when()
                .get("/usuarios")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("size()", greaterThan(0));;
    }

    @Test
    @Order(3)
    public void putUser() {

        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"nome\": \""+userName+"\",\n" +
                        "  \"email\": \""+userEmail+"\",\n" +
                        "  \"password\": \"teste\",\n" +
                        "  \"administrador\": \"true\"\n" +
                        "}")
        .when()
                .put("usuarios/" + Usuario.userId)
        .then()
                .statusCode(201);
    }
    @Test
    @Order(4)
    public void deleteUsuario(){
        given()
                .pathParam("_id", Usuario.userId) // Corrigido para usar Usuario.userId
        .when()
                .delete("/usuarios/{_id}")
        .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK) // Verifique o status code correto
                .body("message", is("Registro excluído com sucesso"));
    }
}
