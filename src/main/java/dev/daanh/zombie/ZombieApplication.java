package dev.daanh.zombie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZombieApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZombieApplication.class, args);
	}
}
