package models;

import java.util.List;

public record OrderLine (
        Integer comboId,
        Integer quantity,
        List<Integer> menuItemIds
    ) {
    public OrderLine {
        quantity = quantity == null ? 1 : quantity;
        menuItemIds = menuItemIds == null ? List.of() : List.copyOf(menuItemIds);
    }
}
