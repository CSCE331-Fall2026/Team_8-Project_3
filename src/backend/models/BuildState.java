package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** In-progress combo selections for the cashier build panel. */
public final class BuildState {

    private final ComboType type;
    private final List<MenuCatalog.MenuItem> selected = new ArrayList<>();

    /** Creates an empty selection for the given combo type. */
    public BuildState(ComboType type) {
        this.type = type;
    }

    /** Returns the combo type being built. */
    public ComboType type() {
        return type;
    }

    /** Adds an item if its category still has room in this combo. */
    public void add(MenuCatalog.MenuItem item) {
        if (canAdd(item)) {
            selected.add(item);
        }
    }

    /** Removes one matching item from the selection. */
    public void remove(MenuCatalog.MenuItem item) {
        selected.remove(item);
    }

    /** Returns how many times the item appears in the selection. */
    public int countOf(MenuCatalog.MenuItem item) {
        return Collections.frequency(selected, item);
    }

    /** Returns whether the selection satisfies the combo's item requirements. */
    public boolean isComplete() {
        if (type.isALaCarte()) {
            return selected.size() == 1;
        }
        return count(MenuCatalog.Category.SIDE) == type.requiredSides()
                && count(MenuCatalog.Category.ENTREE) == type.requiredEntrees()
                && count(MenuCatalog.Category.DRINK) == type.requiredDrinks()
                && count(MenuCatalog.Category.APPETIZER) == type.requiredAppetizers();
    }

    /** Returns the selected item names, or {@code (none)} when empty. */
    public String summary() {
        if (selected.isEmpty()) {
            return "(none)";
        }
        return selected.stream().map(MenuCatalog.MenuItem::name).collect(Collectors.joining(", "));
    }

    /** Returns the combo name and selected items as a cart description. */
    public String cartLine() {
        return type.displayName() + ": " + summary();
    }

    /** Returns the categories available for the current combo type. */
    public List<MenuCatalog.Category> visibleCategories() {
        if (type.isALaCarte()) {
            return List.of(
                    MenuCatalog.Category.SIDE, MenuCatalog.Category.ENTREE, MenuCatalog.Category.APPETIZER);
        }
        List<MenuCatalog.Category> categories = new ArrayList<>();
        for (MenuCatalog.Category category : MenuCatalog.Category.values()) {
            if (type.limitFor(category) > 0) {
                categories.add(category);
            }
        }
        return categories;
    }

    /** Returns an immutable snapshot of the selected items. */
    public List<MenuCatalog.MenuItem> items() {
        return List.copyOf(selected);
    }

    /** Checks whether the item can be added without exceeding its category limit. */
    private boolean canAdd(MenuCatalog.MenuItem item) {
        if (type.isALaCarte()) {
            return selected.isEmpty() && item.category() != MenuCatalog.Category.DRINK;
        }
        return count(item.category()) < type.limitFor(item.category());
    }

    /** Counts selected items in the given category. */
    private long count(MenuCatalog.Category category) {
        return selected.stream().filter(item -> item.category() == category).count();
    }
}
