package dev.daanh.zombie;

import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.domain.world.generator.WorldGenerator;
import dev.daanh.zombie.repository.WorldRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ZombieApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZombieApplication.class, args);
	}
}
