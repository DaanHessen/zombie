package dev.daanh.zombie.domain.person.mind;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class KnowledgeProfile {
    @ElementCollection
    private Set<UUID> knownSettlements;

    @ElementCollection
    private Set<UUID> knownPersons;

    @ElementCollection
    private Set<UUID> readLiterature = new java.util.HashSet<>();

    @ElementCollection
    private List<String> discoveredLore;
}
