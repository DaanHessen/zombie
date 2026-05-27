package dev.daanh.zombie.domain.item;

import dev.daanh.zombie.domain.item.records.ItemCategoryId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inventory_id")
    private List<ItemStack> items;

    public void addItem(ItemTemplate template, ItemStack stack) {
        if (template == null || stack == null) { return; }

        if (template.isInstanceTracked()) {
            this.items.add(stack);
            return;
        }

        for (ItemStack existingStack : this.items) {
            if (existingStack.getItemTemplateId().equals(template.getId())) {
                int spaceLeft = template.getMaxStackSize() - existingStack.getQuantity();

                if (spaceLeft > 0) {
                    if (stack.getQuantity() <= spaceLeft) {
                        existingStack.setQuantity(existingStack.getQuantity() + stack.getQuantity());
                        return;
                    } else {
                        existingStack.setQuantity(template.getMaxStackSize());
                        stack.setQuantity(stack.getQuantity() - spaceLeft);
                    }
                }

                if (stack.getQuantity() > 0) {
                    this.items.add(stack);
                    return;
                }
            }
        }
    }

    public void removeItem(ItemTemplate template, int quantity) {
        if (template == null) { return; }

        var iterator = this.items.iterator();

        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();

            if (stack.getItemTemplateId().equals(template.getId())) {
                if (stack.getQuantity() >= quantity) {
                    stack.setQuantity(stack.getQuantity() - quantity);
                    return;
                }

                quantity -= stack.getQuantity();
                iterator.remove();
            }
        }
    }
}
