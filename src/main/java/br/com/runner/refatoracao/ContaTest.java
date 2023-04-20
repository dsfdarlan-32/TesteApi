package br.com.runner.refatoracao;

import static br.com.utils.ContaUtils.getIdContaPeloNome;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.com.core.BaseTest;

public class ContaTest extends BaseTest {

	@Test
	public void deveInculirContasComSucesso() {
		given().body("{\"nome\": \"Conta inserida\"}").when().post("/contas").then().statusCode(201);
	}

	@Test
	public void deveAlterarContasComSucesso() {
		Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");

		given().body("{\"nome\": \"Conta Alterada\"}").pathParam("id", CONTA_ID).when().put("/contas/{id}").then()
				.statusCode(200).body("nome", is("Conta Alterada"));
	}

	@Test
	public void naoDeveInserirContaComMesmoNome() {
		given().body("{\"nome\": \"Conta mesmo nome\"}").when().post("/contas/").then().statusCode(400).body("error",
				is("Já existe uma conta com esse nome!"));
	}
}
