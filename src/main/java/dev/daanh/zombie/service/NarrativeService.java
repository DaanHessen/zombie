package dev.daanh.zombie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.daanh.zombie.application.dto.response.SurvivorNarrativeContextGroupResponse;
import dev.daanh.zombie.application.dto.response.SurvivorDilemmaResponse;
import dev.daanh.zombie.domain.core.GameTime;
import dev.daanh.zombie.domain.item.ItemTemplate;
import dev.daanh.zombie.domain.item.enums.Freshness;
import dev.daanh.zombie.domain.item.records.WeaponProfile;
import dev.daanh.zombie.domain.weather.WeatherState;
import dev.daanh.zombie.domain.weather.enums.PrecipitationType;
import dev.daanh.zombie.domain.world.Settlement;
import dev.daanh.zombie.domain.world.chunks.Chunk;
import dev.daanh.zombie.domain.survivor.Survivor;
import dev.daanh.zombie.domain.survivor.HealthProfile;
import dev.daanh.zombie.domain.survivor.Condition;
import dev.daanh.zombie.domain.survivor.enums.TraitType;
import dev.daanh.zombie.domain.survivor.Group;
import dev.daanh.zombie.domain.survivor.Relationship;
import dev.daanh.zombie.repository.SettlementRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class NarrativeService {
    private final Configuration freemarkerConfig;
    private final SettlementRepository settlementRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NarrativeService(Configuration freemarkerConfig, SettlementRepository settlementRepository) {
        this.freemarkerConfig = freemarkerConfig;
        this.settlementRepository = settlementRepository;
    }

    private static Map<String, Object> thoughtsData;

    @PostConstruct
    public void init() {
        try (InputStream is = getClass().getResourceAsStream("/narrative/thoughts.json")) {
            if (is == null) {
                log.error("Could not find narrative/thoughts.json in classpath");
                thoughtsData = new HashMap<>();
            } else {
                thoughtsData = objectMapper.readValue(is, Map.class);
                log.info("Successfully loaded thoughts.json templates.");
            }
        } catch (Exception e) {
            log.error("Failed to load thoughts.json", e);
            thoughtsData = new HashMap<>();
        }
    }

    private String pickRandom(String... options) {
        if (options.length == 0) return "";
        return options[ThreadLocalRandom.current().nextInt(options.length)];
    }

    @SuppressWarnings("unchecked")
    private String getThoughtTemplate(String category, String subcategory, Survivor survivor) {
        if (thoughtsData == null) return null;
        Object catObj = thoughtsData.get(category);
        if (catObj instanceof Map) {
            Object subObj = ((Map<?, ?>) catObj).get(subcategory);
            if (subObj instanceof List) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) subObj;
                List<String> matches = new ArrayList<>();
                for (Map<String, Object> item : list) {
                    if (matchesSurvivorState(item, survivor)) {
                        matches.add((String) item.get("text"));
                    }
                }
                if (!matches.isEmpty()) {
                    int index = ThreadLocalRandom.current().nextInt(matches.size());
                    return matches.get(index);
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private boolean matchesSurvivorState(Map<String, Object> item, Survivor survivor) {
        List<String> reqTraits = (List<String>) item.get("traits");
        List<String> reqConditions = (List<String>) item.get("conditions");

        if (survivor == null) {
            return (reqTraits == null || reqTraits.isEmpty()) && (reqConditions == null || reqConditions.isEmpty());
        }

        HealthProfile health = survivor.getHealth();
        if (item.containsKey("minMorale")) {
            int minVal = ((Number) item.get("minMorale")).intValue();
            if (health.getMorale() < minVal) return false;
        }
        if (item.containsKey("maxMorale")) {
            int maxVal = ((Number) item.get("maxMorale")).intValue();
            if (health.getMorale() > maxVal) return false;
        }

        if (reqTraits != null && !reqTraits.isEmpty()) {
            boolean hasAny = false;
            for (String trait : reqTraits) {
                if (survivor.getTraits() != null) {
                    for (TraitType t : survivor.getTraits()) {
                        if (t.name().equalsIgnoreCase(trait)) {
                            hasAny = true;
                            break;
                        }
                    }
                }
            }
            if (!hasAny) return false;
        }

        if (reqConditions != null && !reqConditions.isEmpty()) {
            boolean hasAny = false;
            for (String cond : reqConditions) {
                if (survivor.getConditions() != null) {
                    for (Condition c : survivor.getConditions()) {
                        if (c.getType().name().equalsIgnoreCase(cond)) {
                            hasAny = true;
                            break;
                        }
                    }
                }
            }
            if (!hasAny) return false;
        }

        return true;
    }

    public String generateChunkDescription(Chunk chunk) {
        return generateChunkDescription(chunk, null);
    }

    public String generateChunkDescription(Chunk chunk, Survivor survivor) {
        try {
            if (chunk.getSettlementId() != null && chunk.getSettlement() == null) {
                settlementRepository.findById(chunk.getSettlementId()).ifPresent(chunk::setSettlement);
            }

            if (chunk.getSettlement() != null) {
                Settlement s = chunk.getSettlement();
                String template = s.isGroundZero()
                        ? getThoughtTemplate("chunk_exploration", "urban_ground_zero", survivor)
                        : getThoughtTemplate("chunk_exploration", "urban", survivor);

                if (template != null) {
                    return template.replace("${settlementName}", s.getName())
                            .replace("${population}", String.valueOf(s.getPreApocalypsePopulation()))
                            .replace("${biome}", s.getBiome() != null ? s.getBiome() : "URBAN");
                }

                Template ftlTemplate = freemarkerConfig.getTemplate("narrative/chunk.ftl");
                Map<String, Object> model = new HashMap<>();
                model.put("chunk", chunk);
                StringWriter stringWriter = new StringWriter();
                ftlTemplate.process(model, stringWriter);
                return stringWriter.toString();
            } else {
                String template = getThoughtTemplate("chunk_exploration", "wilderness", survivor);
                if (template != null) {
                    return template;
                }
                return "I'm out in the remote wilderness. There are no signs of human civilization here, but the rustling of the wind still keeps me on edge.";
            }
        } catch (Exception e) {
            log.error("Failed to generate narrative text for chunk", e);
            return "An indescribable area of the wasteland.";
        }
    }

    public String generateItemThought(ItemTemplate template, int quantity, Freshness freshness) {
        return generateItemThought(template, quantity, freshness, null);
    }

    public String generateItemThought(ItemTemplate template, int quantity, Freshness freshness, Survivor survivor) {
        String category = "generic";
        if (freshness != null) {
            switch (freshness) {
                case FRESH -> category = "food_fresh";
                case STALE -> category = "food_stale";
                case SPOILED -> category = "food_spoiled";
                case ROTTEN -> category = "food_rotten";
                case UNRECOGNIZABLE -> category = "food_unrecognizable";
            }
        } else if (template.getProfiles().containsKey(WeaponProfile.class)) {
            category = "weapon";
        } else if (template.getCategoryId() != null && (template.getCategoryId().value().contains("medicine") || template.getCategoryId().value().contains("medical"))) {
            category = "medicine";
        } else if (template.getCategoryId() != null && (template.getCategoryId().value().contains("clothing") || template.getCategoryId().value().contains("equipment"))) {
            category = "clothing";
        }

        String thought = getThoughtTemplate("item_discovery", category, survivor);
        if (thought == null) {
            thought = getThoughtTemplate("item_discovery", "generic", survivor);
        }
        if (thought == null) {
            return "Found some " + template.getName() + ".";
        }
        return thought.replace("${itemName}", template.getName())
                .replace("${quantity}", String.valueOf(quantity));
    }

    public String generateWeatherThought(WeatherState weather) {
        return generateWeatherThought(weather, null);
    }

    public String generateWeatherThought(WeatherState weather, Survivor survivor) {
        String subcategory = "warm";
        if (weather.getPrecipitationType() != null && weather.getPrecipitationType() != PrecipitationType.NONE) {
            if (weather.getPrecipitationType() == PrecipitationType.SNOW) {
                subcategory = "snow";
            } else {
                subcategory = "rain";
            }
        } else if (weather.getTemperature() != null) {
            double celsius = weather.getTemperature().getCelsius();
            if (celsius <= 0) {
                subcategory = "freezing";
            } else if (celsius <= 10) {
                subcategory = "cold";
            } else if (celsius >= 28) {
                subcategory = "hot";
            }
        }

        String thought = getThoughtTemplate("weather", subcategory, survivor);
        if (thought == null) {
            thought = getThoughtTemplate("weather", "warm", survivor);
        }
        if (thought == null) {
            return "Weather is changing.";
        }
        return thought;
    }

    public String generateTimeThought(GameTime time) {
        return generateTimeThought(time, null);
    }

    public String generateTimeThought(GameTime time, Survivor survivor) {
        Instant instant = time.toGlobalInstant();
        int hour = instant.atZone(ZoneOffset.UTC).getHour();

        String subcategory = "day";
        if (hour >= 6 && hour < 12) {
            subcategory = "morning";
        } else if (hour >= 12 && hour < 18) {
            subcategory = "day";
        } else if (hour >= 18 && hour < 22) {
            subcategory = "evening";
        } else {
            subcategory = "night";
        }

        String thought = getThoughtTemplate("time", subcategory, survivor);
        if (thought == null) {
            thought = getThoughtTemplate("time", "day", survivor);
        }
        if (thought == null) {
            return "Time is passing.";
        }
        return thought;
    }

    public String generateSurvivorThought(Survivor survivor, WeatherState weather, GameTime time) {
        HealthProfile health = survivor.getHealth();
        List<String> pool = new ArrayList<>();

        // Priority 1: Pain & Severe Physical Trauma (Overrides general complaints if health < 30)
        if (health.getHealth() < 30) {
            pool.add(pickRandom(
                "I'm bleeding out... everything is getting cold.",
                "Can barely stand. I don't know how much longer my body can hold.",
                "The light is fading. I need medical help immediately."
            ));
        }

        // Priority 2: Critical Physical Needs (Hunger, Hydration, Fatigue, Pain)
        if (survivor.getNeeds().getHunger() > 80) {
            pool.add(pickRandom(
                "My stomach is a hollow pit, eating itself from the inside.",
                "Starvation is claws in my gut; I need to find food soon.",
                "The hunger is constant now, a dull ache that never leaves.",
                "I'd kill for a warm meal right about now. Anything."
            ));
        } else if (survivor.getNeeds().getProtein() < 30) {
            pool.add(pickRandom(
                "My muscles feel completely hollow and weak. I need some real protein.",
                "Lacking strength. A bit of meat or beans would do wonders.",
                "My body is burning its own muscle. I feel so frail."
            ));
        } else if (survivor.getNeeds().getCarbs() < 30) {
            pool.add(pickRandom(
                "Zero energy. My blood sugar must be on the floor.",
                "Feeling faint and lightheaded. Need carbs, quick energy.",
                "My brain feels like fog. I need some sugar or bread to kickstart it."
            ));
        }

        if (100 < 25 || survivor.getNeeds().getThirst() > 80) {
            pool.add(pickRandom(
                "My throat is parched, like swallowing sand.",
                "Dehydration has set in. I need clean water, or any water.",
                "My lips are cracked and dry. Need a drink desperately."
            ));
        }

        if ((100 - survivor.getNeeds().getEnergy()) > 80) {
            pool.add(pickRandom(
                "My eyes are burning. I can barely keep them open.",
                "Exhausted to the core. If I don't rest soon, I'll collapse.",
                "My body is heavy. Every step takes immense effort."
            ));
        }

        if (health.getPain() > 50) {
            pool.add(pickRandom(
                "The pain is white-hot, throbbing with every heartbeat.",
                "Every movement makes me want to scream.",
                "Can't focus. The agony is just too loud."
            ));
        }

        if (pool.isEmpty()) {
            pool.add(pickRandom(
                "My body isn't fighting me today. I feel relatively stable.",
                "At least I'm not starving or dying of thirst right now.",
                "Physically, I'm as good as one can expect in this hell."
            ));
        }

        // Priority 3: Mood, Morale and Sanity
        if (health.getMorale() < 20) {
            pool.add(pickRandom(
                "What's the point of another day? It's all just survival of the miserable.",
                "Sometimes I wonder if it would be easier to just stop running.",
                "The weight of this dead world is crushing me."
            ));
        } else if (health.getMorale() > 80) {
            pool.add(pickRandom(
                "I have to keep pushing. We can rebuild. We have to.",
                "Hope is a rare thing, but I feel it today. We'll make it.",
                "I refuse to let the wasteland win."
            ));
        }

        if (health.getSanity() < 30) {
            pool.add(pickRandom(
                "The shadows... they're moving. I know they are.",
                "I keep hearing my name whispered in the wind. Am I losing my mind?",
                "Just hold it together. Don't look at the eyes in the dark."
            ));
        }

        if (health.getMood() < 30) {
            pool.add(pickRandom(
                "Everything is grating on my nerves. Even the silence.",
                "I'm sick of this. Sick of the dirt, the danger, the decay.",
                "Just want a single moment of peace. Is that too much to ask?"
            ));
        }

        // Priority 4: Days in Apocalypse
        int days = survivor.getDaysInApocalypse();
        if (days < 30) {
            pool.add(pickRandom(
                "It's only been " + days + " days since the world ended, but the old life feels like a dream.",
                "Only " + days + " days in. The shock hasn't fully worked off yet."
            ));
        } else if (days >= 100) {
            pool.add(pickRandom(
                "Day " + days + " of the new era. I've forgotten what a hot shower feels like.",
                "We've survived " + days + " days of this. We are the ghosts of the old world.",
                "Day " + days + ". Another tick on the wall. Another day of ash."
            ));
        }

        // Priority 5: Traits & Relationship Name Interpolation
        List<TraitType> traits = survivor.getTraits();
        if (traits != null && !traits.isEmpty()) {
            TraitType trait = traits.get(ThreadLocalRandom.current().nextInt(traits.size()));
            switch (trait) {
                case BRAVE -> pool.add(pickRandom(
                    "Whatever comes next, I'll face it head-on.",
                    "Fear is just a reaction. Actions are what matter."
                ));
                case COWARD -> pool.add(pickRandom(
                    "Any noise could be the end. I want to hide.",
                    "We shouldn't be out here. We should find a basement and lock the door."
                ));
                case PRAGMATIC -> pool.add(pickRandom(
                    "Sentiment gets you killed. Focus on the inventory and the route.",
                    "It's a simple equation: stay warm, get food, stay alive."
                ));
                case DEPRESSIVE -> pool.add(pickRandom(
                    "We're just delaying the inevitable. The infection wins eventually.",
                    "No one is coming to save us."
                ));
                case LONER -> pool.add(pickRandom(
                    "The silence of the wasteland is better than the bickering of people.",
                    "I don't need anyone else. I'm my own best company."
                ));
                case SOCIAL -> {
                    // Try to interpolate a friend's name if we have one
                    String partner = null;
                    if (survivor.getRelationships() != null && !survivor.getRelationships().isEmpty()) {
                        for (Relationship rel : survivor.getRelationships().values()) {
                            if (rel.getLabel().name().equalsIgnoreCase("FRIEND") || rel.getLabel().name().equalsIgnoreCase("ROMANCE")) {
                                if (rel.getTargetSurvivor() != null) {
                                    partner = rel.getTargetSurvivor().getName();
                                    break;
                                }
                            }
                        }
                    }
                    if (partner != null) {
                        pool.add("I miss " + partner + ". The silence out here is deafening.");
                    } else {
                        pool.add("I need to talk to someone before I start talking to myself.");
                    }
                }
            }
        }

        // Priority 6: Environment
        if (weather != null) {
            if (weather.getPrecipitationType() == PrecipitationType.SNOW) {
                pool.add("Snow covers the dead, but it freezes the living.");
            } else if (weather.getPrecipitationType() == PrecipitationType.RAIN) {
                pool.add("This relentless rain is turning everything to mud and misery.");
            } else if (weather.getTemperature() != null && weather.getTemperature().getCelsius() <= 0) {
                pool.add("The cold is absolute. It's ice in my lungs.");
            } else if (weather.getTemperature() != null && weather.getTemperature().getCelsius() >= 28) {
                pool.add("Suffocating heat. Sweating through my rags.");
            }
        }

        if (time != null) {
            java.time.Instant instant = time.toGlobalInstant();
            int hour = instant.atZone(ZoneOffset.UTC).getHour();
            if (hour >= 18 || hour < 6) {
                pool.add("The night belongs to the walkers. Stay quiet.");
            } else if (hour >= 6 && hour < 12) {
                pool.add("Another sunrise. A clean slate for scavenging.");
            }
        }

        // Shuffle pool & construct a natural paragraph containing 3 to 4 sentences
        Collections.shuffle(pool);
        int sentencesCount = Math.min(pool.size(), ThreadLocalRandom.current().nextInt(3, 5));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sentencesCount; i++) {
            sb.append(pool.get(i));
            if (i < sentencesCount - 1) {
                sb.append(pickRandom(" ", " ... ", " Yet, ", " But, "));
            }
        }

        return sb.toString();
    }

    public List<String> generateGameplayModifiers(Survivor survivor) {
        List<String> modifiers = new ArrayList<>();
        HealthProfile health = survivor.getHealth();

        if (survivor.getTraits() != null) {
            for (TraitType trait : survivor.getTraits()) {
                switch (trait) {
                    case BRAVE -> modifiers.add("Brave (+15% Combat Damage, -30% Panic rate)");
                    case COWARD -> modifiers.add("Coward (-25% Aiming Accuracy under threat, +50% Panic rate)");
                    case PRAGMATIC -> modifiers.add("Pragmatic (+10% Scavenging efficiency, -10% Morale loss from eating poor food)");
                    case DEPRESSIVE -> modifiers.add("Depressive (-20% Morale recovery, faster sanity decline)");
                    case LONER -> modifiers.add("Loner (+15% Work speed when isolated, -20% Morale loss when alone)");
                    case SOCIAL -> modifiers.add("Social (+15% Morale recovery when grouped, -20% Work speed when alone)");
                }
            }
        }

        if (survivor.getNeeds().getHunger() > 75) {
            modifiers.add("Starving (-30% Stamina recovery, -20% Hacking speed)");
        }
        if (100 < 25 || survivor.getNeeds().getThirst() > 75) {
            modifiers.add("Dehydrated (-40% Max speed, -15% Strength)");
        }
        if (health.getPain() > 50) {
            modifiers.add("Severe Pain (-25% Agility, -10% Intelligence)");
        }
        if (health.getSanity() < 30) {
            modifiers.add("Demented (Randomly halts actions, high hallucination risk)");
        }

        return modifiers;
    }

    public SurvivorDilemmaResponse checkDilemmas(Survivor survivor) {
        HealthProfile health = survivor.getHealth();
        if (health.getSanity() < 25) {
            return new SurvivorDilemmaResponse(
                survivor.getId(),
                survivor.getName(),
                "MENTAL_BREAK",
                "I keep hearing things in the shadows... they are coming for me. I can't stay here anymore.",
                List.of(
                    new SurvivorDilemmaResponse.DilemmaOption("RETREAT", "Flee back to shelter", "Spend 20 energy, decreases panic risk"),
                    new SurvivorDilemmaResponse.DilemmaOption("MEDICATE", "Take calming pills", "Consumes 1 medicine, increases sanity by 30"),
                    new SurvivorDilemmaResponse.DilemmaOption("PUSH_THROUGH", "Push through the voices", "Reduces sanity by 10, risk of permanent trauma")
                )
            );
        }

        if (survivor.getNeeds().getHunger() > 85) {
            return new SurvivorDilemmaResponse(
                survivor.getId(),
                survivor.getName(),
                "STARVATION_DESPERATION",
                "I am starving to death. There's some moldy trash nearby... maybe there is something edible inside.",
                List.of(
                    new SurvivorDilemmaResponse.DilemmaOption("EAT_TRASH", "Scavenge the trash pile", "Restores 25 hunger, 40% chance of severe poisoning"),
                    new SurvivorDilemmaResponse.DilemmaOption("BEG", "Ask group members for food", "Reduces relationship score, but might get food"),
                    new SurvivorDilemmaResponse.DilemmaOption("STARVE", "Hold out a bit longer", "Suffer -15 health and severe pain")
                )
            );
        }

        return null;
    }

    public String resolveDilemma(Survivor survivor, String optionId) {
        switch (optionId) {
            case "RETREAT" -> { return survivor.getName() + " retreated to shelter, shaking but safe for now."; }
            case "MEDICATE" -> { return survivor.getName() + " took the pills. The whispering subsided."; }
            case "PUSH_THROUGH" -> { return survivor.getName() + " ignored the warnings and pushed forward, eyes wild."; }
            case "EAT_TRASH" -> { return survivor.getName() + " scavenged the moldy trash. It was disgusting, but filled the void."; }
            case "BEG" -> { return survivor.getName() + " begged others for scraps, receiving cold glares but saving their life."; }
            case "STARVE" -> { return survivor.getName() + " chose to starve, slipping deeper into weakness."; }
            default -> { return survivor.getName() + " made a choice."; }
        }
    }

    public SurvivorNarrativeContextGroupResponse generateGroupNarrative(Group group, int daysInApocalypse, Double celsius, PrecipitationType precipitation) {
        WeatherState weather = null;
        if (celsius != null || precipitation != null) {
            weather = new WeatherState();
            if (celsius != null) weather.setTemperature(new dev.daanh.zombie.domain.weather.Temperature(celsius));
            if (precipitation != null) weather.setPrecipitationType(precipitation);
        }

        GameTime time = new GameTime(1200); // Midday fallback
        List<SurvivorNarrativeContextGroupResponse.SurvivorNarrativeState> states = new ArrayList<>();

        if (group.getSurvivors() != null) {
            for (Survivor s : group.getSurvivors()) {
                String thought = generateSurvivorThought(s, weather, time);
                states.add(new SurvivorNarrativeContextGroupResponse.SurvivorNarrativeState(
                    s.getId(),
                    s.getName(),
                    s.getHealth().getHealth(),
                    s.getHealth().getMood(),
                    s.getHealth().getMorale(),
                    s.getHealth().getSanity(),
                    s.getNeeds().getHunger(),
                    s.getNeeds().getThirst(),
                    s.getNeeds().getProtein(),
                    s.getNeeds().getCarbs(),
                    getTraitList(s.getTraits()),
                    getConditionList(s.getConditions()),
                    thought
                ));
            }
        }

        return new SurvivorNarrativeContextGroupResponse(group.getId(), daysInApocalypse, states);
    }

    private List<String> getTraitList(List<TraitType> traits) {
        if (traits == null) return List.of();
        return traits.stream().map(Enum::name).toList();
    }

    private List<String> getConditionList(List<Condition> conditions) {
        if (conditions == null) return List.of();
        return conditions.stream().map(c -> c.getType().name()).toList();
    }

    @SuppressWarnings("unchecked")
    public String generateNarratorLog(String eventType, Map<String, String> context) {
        if (thoughtsData == null) return "Simulation Event: " + eventType;
        Object catObj = thoughtsData.get("narrator_events");
        if (catObj instanceof Map) {
            Object subObj = ((Map<?, ?>) catObj).get(eventType);
            if (subObj instanceof List) {
                List<String> list = (List<String>) subObj;
                if (!list.isEmpty()) {
                    int index = ThreadLocalRandom.current().nextInt(list.size());
                    String template = list.get(index);
                    for (Map.Entry<String, String> entry : context.entrySet()) {
                        template = template.replace("${" + entry.getKey() + "}", entry.getValue());
                    }
                    return template;
                }
            }
        }
        return "Simulation Event: " + eventType + " " + context.toString();
    }
}
