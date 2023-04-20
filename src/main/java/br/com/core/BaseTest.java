package br.com.core;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;

public class BaseTest implements Constantes {

	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = APP_BASE_URL;
		RestAssured.port = APP_POT;
		RestAssured.basePath = APP_BASE_PATH;
		
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();	
		reqBuilder.setContentType(APP_CONTENT_TYPE);
		RestAssured.requestSpecification = reqBuilder.build();
		
		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
		resBuilder.expectResponseTime(Matchers.lessThan(MAX_TIMEOUT));
		RestAssured.responseSpecification =  resBuilder.build();
		
		//habilita log quando houver erro na acertiva
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}
}
