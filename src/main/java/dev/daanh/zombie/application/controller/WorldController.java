package dev.daanh.zombie.application.controller;

import dev.daanh.zombie.application.dto.response.WorldResponse;
import dev.daanh.zombie.service.WorldService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/world")
@AllArgsConstructor
public class WorldController {
    private final WorldService worldService;

    @PostMapping("/seed")
    public WorldResponse seedWorld() throws Exception {
        return worldService.seedWorld();
    }
}
