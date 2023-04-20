package br.com.runner.refatoracao;

import static br.com.utils.ContaUtils.getIdContaPeloNome;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.com.core.BaseTest;

public class SaldoTest extends BaseTest {

	@Test
	public void deveCalcularSaldoContas() {
		Integer CONTA_ID = getIdContaPeloNome("Conta para saldo");

		given().when().get("/saldo").then().statusCode(200).body("find{it.conta_id == " + CONTA_ID + "}.saldo",
				is("534.00"));
	}
}