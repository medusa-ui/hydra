package io.getmedusa.hydra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class HydraApplication {

	public static void main(String[] args) {
		BlockHound.install();
		SpringApplication.run(HydraApplication.class, args);
	}

}
