package br.com.runner.refatoracao;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.com.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest extends BaseTest {

	@Test
	public void ct11_naoDeveAcessarApiSemToken() {
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");

		given().when().get("/contas").then().statusCode(401);
	}
}
