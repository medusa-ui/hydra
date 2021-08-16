package io.getmedusa.hydra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.blockhound.BlockHound;

@SpringBootApplication
@EnableScheduling
public class HydraApplication {

	public static void main(String[] args) {
		BlockHound.install();
		SpringApplication.run(HydraApplication.class, args);
	}

}
