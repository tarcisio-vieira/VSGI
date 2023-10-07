package br.com.vsgi.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoreApplication {

	/**
	 * Logger LOGGER
	 */
	private static final Logger LOGGER = LogManager.getLogger(CoreApplication.class);

	public static void main(String[] args) {
		LOGGER.info("Initializing Core Project");
		SpringApplication.run(CoreApplication.class, args);
		LOGGER.info("Main Project initialized");
	}
}
