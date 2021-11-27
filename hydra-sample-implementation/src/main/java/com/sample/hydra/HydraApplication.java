package com.sample.hydra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HydraApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(HydraApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
