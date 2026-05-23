package dev.daanh.zombie.controller;

import dev.daanh.zombie.service.WorldService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/world")
@AllArgsConstructor
public class WorldController {
    private final WorldService worldGeneratorService;

    @PostMapping("/generate")
    public void generateWorld() throws Exception {
        worldGeneratorService.seedWorld();
    }
}
