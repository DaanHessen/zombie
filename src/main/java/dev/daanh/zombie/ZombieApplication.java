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

	@Bean
	CommandLineRunner runWorldGenerator(WorldRepository worldRepository) {
		return args -> {
			System.out.println("========================================");
			System.out.println("Starting World Generation...");
			long startTime = System.currentTimeMillis();

			WorldGenerator generator = new WorldGenerator();
			World world = generator.generateWorld();

			long genTime = System.currentTimeMillis();
			System.out.println("Generated World in Memory in: " + (genTime - startTime) + "ms");

			System.out.println("Saving generated World to database (this might take a few seconds due to 33,000+ settlements)...");
			worldRepository.save(world);

			long endTime = System.currentTimeMillis();
			System.out.println("Successfully saved World to Database!");
			System.out.println("Database Save Time: " + (endTime - genTime) + "ms");
			System.out.println("Total Execution Time: " + (endTime - startTime) + "ms");
			System.out.println("Continents: " + world.getContinents().size());
			System.out.println("Water Bodies: " + world.getWaterBodies().size());
			System.out.println("========================================");
		};
	}
}
