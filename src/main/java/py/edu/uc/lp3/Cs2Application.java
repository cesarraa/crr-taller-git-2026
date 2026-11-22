package py.edu.uc.lp3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Cs2Application {

	public static void main(String[] args) {
		SpringApplication.run(Cs2Application.class, args);
	}

}
