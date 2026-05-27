package dev.daanh.zombie.domain.person.ai;

import dev.daanh.zombie.domain.person.Person;

import java.util.ArrayList;
import java.util.List;

public abstract class ThinkNode {
    protected List<ThinkNode> subNodes = new ArrayList<>();

    public void addSubNode(ThinkNode node) {
        this.subNodes.add(node);
    }

    /**
     * Attempts to find a Job for the given person.
     * @param person The person evaluating the tree
     * @return A valid Job, or null if this node cannot assign one.
     */
    public abstract Job tryIssueJob(Person person);
}
