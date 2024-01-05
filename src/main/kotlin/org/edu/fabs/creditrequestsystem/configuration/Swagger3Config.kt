package org.edu.fabs.creditrequestsystem.configuration

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean

//@Configuration
class Swagger3Config {

    @Bean
    fun publicApi(): OpenAPI {
        return OpenAPI()
            .components(Components())
            .info(Info()
                .title("Credit Application System API")
                .description("This is the API documentation for REST API"))
    }

}