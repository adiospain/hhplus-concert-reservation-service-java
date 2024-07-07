package io.hhplus.concert_reservation_service_java.core.configuration.swagger;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER,
    name = "Authorization", description = "Prefix Required! Add 'Bearer ' before token!"
)
public class SwaggerConfig{


  @Bean
  public GroupedOpenApi api() {
    return GroupedOpenApi.builder()
        .group("api")
        .addOpenApiCustomizer(customOpenApi())
        .pathsToMatch("/**")
        .build();
  }

  private OpenApiCustomizer customOpenApi (){
    return openApi -> openApi.info(
            new Info().title("콘서트 예약 서비스 API")
                .description("콘서트 예약 서비스 API 문서")
                .version("alpha 1.0"))
        .addSecurityItem(new SecurityRequirement().addList("Authorization"))
        ;
  }

}