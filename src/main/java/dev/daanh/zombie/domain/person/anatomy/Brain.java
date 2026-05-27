package dev.daanh.zombie.domain.person.anatomy;

import dev.daanh.zombie.domain.core.BaseEntity;
import dev.daanh.zombie.domain.person.mind.NeedsProfile;
import dev.daanh.zombie.domain.person.mind.KnowledgeProfile;
import dev.daanh.zombie.domain.person.mind.Memory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Brain extends BaseEntity {
    @Embedded
    private NeedsProfile needs = new NeedsProfile();

    @Embedded
    private KnowledgeProfile knowledge = new KnowledgeProfile();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "brain_id")
    private List<Memory> coreMemories = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "brain_id")
    private List<Memory> longTermMemories = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "brain_id")
    private List<Memory> shortTermMemories = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_job_id")
    private dev.daanh.zombie.domain.person.ai.Job currentJob;
}
