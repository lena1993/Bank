package com.bank.simulator.Config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket productApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bank.simulator"))
                .paths(postPaths())
                .build()
                .apiInfo(metaInfo());
    }

    private Predicate<String> postPaths() {
        return  or(regex("/rest.*"), regex("/bank.*"));
    }

    private ApiInfo metaInfo(){
        ApiInfo apiInfo =  new ApiInfo(
                "Spring bootswagger example API",
                "Spring noot xagger ex for me",
                "1,0",
                "Terms of services",
                new Contact("TechPrimers","https://www.youtube.com/TechPrimers",
                        "techprimerschannel@gmail.com"),
                "Apache License Version 2.0",
                "https://www.apache.org/licesen.html"
        );
        return apiInfo;
    }

}
