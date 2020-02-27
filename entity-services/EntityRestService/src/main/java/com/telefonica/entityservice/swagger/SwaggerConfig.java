package com.telefonica.entityservice.swagger;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket productApi() {

		ParameterBuilder parameterBuilder = new ParameterBuilder();
		List<Parameter> headerParameters = new ArrayList<>();

		parameterBuilder.name("Content-Type").modelRef(new ModelRef("string")).parameterType("header").required(true)
				.defaultValue("application/json").build();
		headerParameters.add(parameterBuilder.build());

		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.telefonica.entityservice.controller"))
				.paths(PathSelectors.any()).build().apiInfo(metaData()).globalOperationParameters(headerParameters);
	}

	private ApiInfo metaData() {
		return new ApiInfoBuilder().title("Entity-Service").description("Servicios RESTs ABM de Entidades.")
				.version("1.0-SNAPSHOT").build();
	}

}
