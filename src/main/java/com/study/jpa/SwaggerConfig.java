package com.study.jpa;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 설정을 담당하는 클래스라는 것을 지정하는 아노테이션
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Spring boot Board API Example")
                        .description("Spring boot Board API 예시 프로젝트 입니다.")
                        .version("v1.0.0")
                );
    }
}
