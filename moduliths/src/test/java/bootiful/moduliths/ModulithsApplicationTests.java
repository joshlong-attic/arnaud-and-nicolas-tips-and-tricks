package bootiful.moduliths;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;


class ModulithsApplicationTests {

	private final ApplicationModules modules =
			ApplicationModules.of(ModulithsApplication.class) ;

	@Test
	void contextLoads()  throws Exception {
		// archunit
		this.modules.verify();
	}

}
