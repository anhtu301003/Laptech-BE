package com.project.LaptechBE;

import com.project.LaptechBE.untils.Endpoints;
import jakarta.ws.rs.ApplicationPath;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
public class LaptechBeApplication {
	public static void main(String[] args) {
		SpringApplication.run(LaptechBeApplication.class, args);
	}

}
