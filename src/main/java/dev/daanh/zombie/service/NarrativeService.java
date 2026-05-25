package dev.daanh.zombie.service;

import dev.daanh.zombie.domain.world.chunks.Chunk;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class NarrativeService {
    private final Configuration freemarkerConfig;

    public String generateChunkDescription(Chunk chunk) {
        try {
            Template template = freemarkerConfig.getTemplate("narrative/chunk.ftl");
            Map<String, Object> model = new HashMap<>();
            model.put("chunk", chunk);
            
            StringWriter stringWriter = new StringWriter();
            template.process(model, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            log.error("Failed to generate narrative text for chunk", e);
            return "An indescribable area of the wasteland.";
        }
    }
}
