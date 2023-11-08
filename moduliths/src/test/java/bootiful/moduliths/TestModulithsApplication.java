package bootiful.moduliths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;


@TestConfiguration(proxyBeanMethods = false)
public class TestModulithsApplication {

	// testcontainers
//	@Bean
//	@ServiceConnection
//	@RestartScope
//	PostgreSQLContainer<?> postgresContainer() {
//		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
//	}

	public static void main(String[] args) {
		SpringApplication
				.from(ModulithsApplication::main)
				.with(TestModulithsApplication.class)
				.run(args);
	}

}
