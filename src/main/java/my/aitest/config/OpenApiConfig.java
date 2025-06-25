package my.aitest.config;


import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Test RAG API")
            .version("1.0.0")
            .description("API documentation for My Application"))
        .servers(List.of(
            new Server().url("http://127.0.0.1:8080").description("Local server")
        ));
  }

}
