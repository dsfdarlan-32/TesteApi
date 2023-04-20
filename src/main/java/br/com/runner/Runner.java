package br.com.runner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.com.core.BaseTest;
import br.com.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Runner extends BaseTest{
	private static String CONTA_NOME  =  "Conta "  + System.nanoTime();
	private static Integer CONTA_ID;
	private static Integer MOV_ID;
	
	
	@BeforeClass
	public static  void login() {
		Map<String,String> login = new HashMap<String, String>();
		login.put("email", "dsfdarlan-32@hotmail.com");
		login.put("senha", "123456");
		
		String TOKEN = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
//			.log().all()
		 	.statusCode(200)
		 	.extract().path("token");
		
		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
	}
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
//		mov.setId(id);
		mov.setDescricao("Descricao da movimentacao");
		mov.setEnvolvido("Envolvido na mov");
		mov.setTipo("REC");
		mov.setData_transacao(DataUtils.getDataDiferencaDias(-1));
		mov.setData_pagamento(DataUtils.getDataDiferencaDias(5));
		mov.setValor(100f);
		mov.setStatus(true);
		return mov;
	}

	@Test
	public void ct01_deveInculirContasComSucesso() {
		 CONTA_ID = given()
//				 .header("Authorization", "JWT " + TOKEN)
		 	.body("{\"nome\": \"" + CONTA_NOME + "\"}")
		 .when()
			.post("/contas")
		.then()
		 	.statusCode(201)
		 	.extract().path("id")
		 ;
	}
	
	@Test
	public void ct02_deveAlterarContasComSucesso() {
		 given()
		 	.body("{\"nome\": \"" + CONTA_NOME + " Alterada\"}")
		 	.pathParam("id", CONTA_ID)
		 .when()
			.put("/contas/{id}")
		.then()
		 	.statusCode(200)
		 	.body("nome", is("" + CONTA_NOME + " Alterada"))
		;
	}

	@Test
	public void ct03_naoDeveInserirContaComMesmoNome() {
		given()
		 	.body("{\"nome\": \""+ CONTA_NOME +" Alterada\"}")
		 .when()
			.post("/contas/")
		.then()
			.statusCode(400)
		 	.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void ct04_deveInserirMovimentacaoComSucesso() {
		Movimentacao mov = getMovimentacaoValida();

		MOV_ID = given()
		 	.body(mov)
		 .when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void ct05_deveValidarCamposObrigatoriosNaMovimentacao() {
		given()
		 	.body("{}")
		 .when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"
				))
		;
	}
	
	@Test
	public void ct06_naoDeveInserirMovimentacaoComDataFutura() {
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao(DataUtils.getDataDiferencaDias(2));

		given()
		 	.body(mov)
		 .when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg",hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		;
	}
	
	@Test
	public void ct07_naoDeveRemoverContaComMovimentacao() {
		given()
		 	.pathParam("id", CONTA_ID)
		 .when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void ct08_deveCalcularSaldoContas() {
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == " + CONTA_ID + "}.saldo", is("100.00"))
		;
	}

	@Test
	public void ct09_deveRemoverMovimentacao(){
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	
	@Test
	public void ct10_naoDeveAcessarApiSemToken() {
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");

		given()
		.when()
			.get("/contas")
		.then()
		 	.statusCode(401)
		;
	}
}