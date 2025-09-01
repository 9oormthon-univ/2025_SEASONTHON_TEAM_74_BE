package com.example.demo.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Authentication", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.APIKEY) // APIKEY 방식으로 설정
                                .in(SecurityScheme.In.HEADER) // 헤더에서 인증 값 읽기
                                .description("Enter your JWT token without 'Bearer ' prefix"))
                        .addSecuritySchemes("Refresh Token", new SecurityScheme()
                                .name("Refresh-Token")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("Enter your refresh token without 'Bearer ' prefix")))
                .addSecurityItem(new SecurityRequirement().addList("Authentication"))
                .addSecurityItem(new SecurityRequirement().addList("Refresh Token"))
                .addServersItem(new Server().url("/").description("https 설정"))
                .info(new Info()
                        .title("API 문서")
                        .version("1.0.0")
                        .description("API 문서 without Bearer prefix in Authorization(AccessToken,Refresh Token) header"));
    }
}
