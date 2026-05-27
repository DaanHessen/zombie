# Simulation Engine & Advanced Narrative Design

This document details the procedural simulation logic, Utility-Based Needs, the Narrative Memory Graph, and the Clashing Desires system.

---

## 1. Domain Additions (UML Assumptions)

- `Survivor` class contains:
  - `- memories: List<Memory>`
- `Memory` class (extends `BaseState`):
  - `- id: UUID`
  - `- eventType: String` (e.g., `"HEARD_GUNSHOT"`, `"ATE_ROTTEN_FOOD"`, `"FRIEND_DEATH"`)
  - `- targetName: String` (context name)
  - `- tickTimestamp: long` (game time tick)
  - `- intensity: int` (emotional weight, 1-100)
  - `- permanent: boolean` (if true, bypasses standard decay)

---

## 2. Core Needs & Utility AI (SimulationEngine)

Every hour (game tick), the simulation decays needs (Hunger, Thirst, Fatigue, Sanity, Mood, Carbs, Protein, Hydration) and calculates utility scores for each potential activity:

- `SCAVENGE`, `REST`, `FORTIFY`, `CRAFT`, `SOCIALIZE`, `IDLE`, `WANDER_PANIC`.

The AI executes the action with the highest calculated utility:

$$\text{Utility} = \text{Need Deficit} \times \text{Trait Modifier} \times \text{Environmental Urgency}$$

---

## 3. Advanced Narrative Service

The `NarrativeService` builds survivor thoughts dynamically from three primary procedural inputs:

1. **Active Memories**: Pulls recent or high-intensity memories from the survivor's graph (e.g., *“I can still hear the gunshots from yesterday...”*).
2. **Clashing Desires**: Triggers an inner-conflict thought if two utilities (e.g., eating vs hiding) are within 10% of each other.
3. **Priority Needs**: Critical states (bleeding, starvation, severe pain) override weather/time reflections.
