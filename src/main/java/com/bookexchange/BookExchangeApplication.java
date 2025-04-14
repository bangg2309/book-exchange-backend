package com.bookexchange;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition(

)
@SpringBootApplication
public class BookExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookExchangeApplication.class, args);
	}

}
