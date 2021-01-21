package de.opengamebackend.collection;

import de.opengamebackend.util.EnableOpenGameBackendUtils;
import de.opengamebackend.util.config.ApplicationConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableOpenGameBackendUtils
public class CollectionApplication {
	@Bean
	public OpenAPI customOpenAPI(ApplicationConfig applicationConfig) {
		return new OpenAPI().info(new Info()
				.title("Open Game Backend Collection")
				.version(applicationConfig.getVersion())
				.description("Provides access to all items owned by the players, along with their types and loadouts.")
				.license(new License().name("MIT").url("https://github.com/open-game-backend/collection/blob/develop/LICENSE")));
	}

	public static void main(String[] args) {
		SpringApplication.run(CollectionApplication.class, args);
	}
}
