package dev.daanh.zombie.application.controller;

import dev.daanh.zombie.application.dto.response.SurvivorNarrativeContextGroupResponse;
import dev.daanh.zombie.application.dto.response.SurvivorDilemmaResponse;
import dev.daanh.zombie.domain.core.GameTime;
import dev.daanh.zombie.domain.item.ItemTemplate;
import dev.daanh.zombie.domain.item.enums.Freshness;
import dev.daanh.zombie.domain.survivor.Survivor;
import dev.daanh.zombie.domain.survivor.Group;
import dev.daanh.zombie.domain.weather.WeatherState;
import dev.daanh.zombie.domain.weather.enums.PrecipitationType;
import dev.daanh.zombie.repository.ItemTemplateRepository;
import dev.daanh.zombie.repository.SurvivorRepository;
import dev.daanh.zombie.repository.GroupRepository;
import dev.daanh.zombie.service.NarrativeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/narrative")
@AllArgsConstructor
public class NarrativeController {
    private final NarrativeService narrativeService;
    private final ItemTemplateRepository itemTemplateRepository;
    private final SurvivorRepository survivorRepository;
    private final GroupRepository groupRepository;

    @GetMapping("/item-thought")
    public String getItemThought(
            @RequestParam UUID templateId,
            @RequestParam int quantity,
            @RequestParam(required = false) Freshness freshness,
            @RequestParam(required = false) UUID survivorId
    ) {
        ItemTemplate template = itemTemplateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));
        Survivor survivor = null;
        if (survivorId != null) {
            survivor = survivorRepository.findById(survivorId)
                    .orElseThrow(() -> new IllegalArgumentException("Survivor not found: " + survivorId));
        }
        return narrativeService.generateItemThought(template, quantity, freshness, survivor);
    }

    @GetMapping("/weather-thought")
    public String getWeatherThought(
            @RequestParam double celsius,
            @RequestParam PrecipitationType precipitationType,
            @RequestParam(required = false) UUID survivorId
    ) {
        WeatherState state = new WeatherState();
        state.setTemperature(new dev.daanh.zombie.domain.weather.Temperature(celsius));
        state.setPrecipitationType(precipitationType);
        Survivor survivor = null;
        if (survivorId != null) {
            survivor = survivorRepository.findById(survivorId)
                    .orElseThrow(() -> new IllegalArgumentException("Survivor not found: " + survivorId));
        }
        return narrativeService.generateWeatherThought(state, survivor);
    }

    @GetMapping("/time-thought")
    public String getTimeThought(
            @RequestParam long ticks,
            @RequestParam(required = false) UUID survivorId
    ) {
        Survivor survivor = null;
        if (survivorId != null) {
            survivor = survivorRepository.findById(survivorId)
                    .orElseThrow(() -> new IllegalArgumentException("Survivor not found: " + survivorId));
        }
        return narrativeService.generateTimeThought(new GameTime(ticks), survivor);
    }

    @GetMapping("/survivor-thought/{survivorId}")
    public String getSurvivorThought(
            @PathVariable UUID survivorId,
            @RequestParam(required = false) Double celsius,
            @RequestParam(required = false) PrecipitationType precipitation,
            @RequestParam(required = false) Long ticks
    ) {
        Survivor survivor = survivorRepository.findById(survivorId)
                .orElseThrow(() -> new IllegalArgumentException("Survivor not found: " + survivorId));

        WeatherState weather = null;
        if (celsius != null || precipitation != null) {
            weather = new WeatherState();
            if (celsius != null) weather.setTemperature(new dev.daanh.zombie.domain.weather.Temperature(celsius));
            if (precipitation != null) weather.setPrecipitationType(precipitation);
        }

        GameTime time = ticks != null ? new GameTime(ticks) : null;

        return narrativeService.generateSurvivorThought(survivor, weather, time);
    }

    @GetMapping("/group-thought/{groupId}")
    public SurvivorNarrativeContextGroupResponse getGroupNarrative(
            @PathVariable UUID groupId,
            @RequestParam int daysInApocalypse,
            @RequestParam(required = false) Double celsius,
            @RequestParam(required = false) PrecipitationType precipitation
    ) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));

        return narrativeService.generateGroupNarrative(group, daysInApocalypse, celsius, precipitation);
    }

    @GetMapping("/survivor-dilemma/{survivorId}")
    public SurvivorDilemmaResponse checkSurvivorDilemma(@PathVariable UUID survivorId) {
        Survivor survivor = survivorRepository.findById(survivorId)
                .orElseThrow(() -> new IllegalArgumentException("Survivor not found: " + survivorId));
        return narrativeService.checkDilemmas(survivor);
    }

    @PostMapping("/dilemma-resolve/{survivorId}")
    public String resolveSurvivorDilemma(
            @PathVariable UUID survivorId,
            @RequestParam String optionId
    ) {
        Survivor survivor = survivorRepository.findById(survivorId)
                .orElseThrow(() -> new IllegalArgumentException("Survivor not found: " + survivorId));
        return narrativeService.resolveDilemma(survivor, optionId);
    }

    @PostMapping("/narrator-log")
    public String generateNarratorLog(
            @RequestParam String eventType,
            @RequestBody Map<String, String> context
    ) {
        return narrativeService.generateNarratorLog(eventType, context);
    }
}
