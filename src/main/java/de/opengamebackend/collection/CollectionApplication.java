package de.opengamebackend.collection;

import de.opengamebackend.util.EnableOpenGameBackendUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableOpenGameBackendUtils
public class CollectionApplication {
	public static void main(String[] args) {
		SpringApplication.run(CollectionApplication.class, args);
	}
}
