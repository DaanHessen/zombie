package dev.daanh.zombie;

import dev.daanh.zombie.config.GameConfig;
import dev.daanh.zombie.domain.location.Location;
import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.domain.world.World;
import dev.daanh.zombie.repository.ChunkRepository;
import dev.daanh.zombie.repository.LocationRepository;
import dev.daanh.zombie.repository.SettlementRepository;
import dev.daanh.zombie.repository.WorldRepository;
import dev.daanh.zombie.service.ChunkService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class ZombieApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZombieApplication.class, args);
	}

	@Bean
	public CommandLineRunner testLocationSeeder(
			ChunkService chunkService,
			WorldRepository worldRepository,
			SettlementRepository settlementRepository,
			LocationRepository locationRepository,
			ChunkRepository chunkRepository,
			GameConfig config
	) {
		return args -> {
			System.out.println("==================================================");
			System.out.println("TESTING HYBRID LOCATION GENERATOR FOR HILVERSUM...");
			System.out.println("==================================================");

			chunkRepository.deleteAll();

			World world = new World();
			world.setName("Test World");
			world = worldRepository.save(world);

			List<Settlement> hilversums = settlementRepository.findByNameIgnoreCase("hilversum");
			if (hilversums.isEmpty()) {
				System.err.println("Could not find Hilversum in the database!");
				return;
			}
			Settlement hilversum = hilversums.get(0);
			System.out.println("Found Settlement: " + hilversum.getName() + " (Population: " + hilversum.getPreApocalypsePopulation() + ")");

			System.out.println("Triggering Chunk Generation (This will call OsmLocationSeeder and LocationGenerator)...");
			
			config.getWorld().getChunk().setChunkGenerationRadius(0);
			chunkService.getChunks(hilversum.getCoordinates(), world);

			List<Location> generatedLocations = locationRepository.findBySettlementId(hilversum.getId());
			System.out.println("==================================================");
			System.out.println("GENERATED LOCATIONS IN HILVERSUM:");
			System.out.println("Total Generated: " + generatedLocations.size());
			for (Location loc : generatedLocations) {
				System.out.println(" - " + loc.getName() + " | Type: " + loc.getType() + " | Category: " + loc.getCategory() + " | Size: " + loc.getSize());
			}
			System.out.println("==================================================");
		};
	}
}
