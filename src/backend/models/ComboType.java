package models;

public enum ComboType {
    BOWL(1, "Bowl", 1, 1, 0, 0),
    PLATE(2, "Plate", 1, 2, 0, 0),
    BIGGER_PLATE(3, "Bigger Plate", 1, 3, 0, 0),
    A_LA_CARTE(4, "A La Carte", 0, 0, 0, 0),
    DRINK(5, "Drink", 0, 0, 1, 0),
    BOWL_COMBO(6, "Bowl Combo", 1, 1, 1, 0),
    PLATE_COMBO(7, "Plate Combo", 1, 2, 1, 0),
    BIGGER_PLATE_COMBO(8, "Bigger Plate Combo", 1, 3, 1, 0),
    APPETIZER(9, "Appetizer", 0, 0, 0, 1);

    private final int comboId;
    private final String displayName;
    private final int requiredSides;
    private final int requiredEntrees;
    private final int requiredDrinks;
    private final int requiredAppetizers;

    /** Defines a combo and its required item counts and starting price. */
    ComboType(
            int comboId,
            String displayName,
            int requiredSides,
            int requiredEntrees,
            int requiredDrinks,
            int requiredAppetizers
        ) {
        this.comboId = comboId;
        this.displayName = displayName;
        this.requiredSides = requiredSides;
        this.requiredEntrees = requiredEntrees;
        this.requiredDrinks = requiredDrinks;
        this.requiredAppetizers = requiredAppetizers;
    }

    /** Returns this combo's database identifier. */
    public int comboId() {
        return comboId;
    }

    /** Returns the display name shown in the cashier interface. */
    public String displayName() {
        return displayName;
    }

    /** Returns the required number of sides. */
    public int requiredSides() {
        return requiredSides;
    }

    /** Returns the required number of entrees. */
    public int requiredEntrees() {
        return requiredEntrees;
    }

    /** Returns the required number of drinks. */
    public int requiredDrinks() {
        return requiredDrinks;
    }

    /** Returns the required number of appetizers. */
    public int requiredAppetizers() {
        return requiredAppetizers;
    }

    /** Returns the selection limit for the specified menu category. */
    public int limitFor(MenuItemType type) {
        return switch (type) {
            case SIDE -> requiredSides;
            case ENTREE -> requiredEntrees;
            case DRINK -> requiredDrinks;
            case APPETIZER -> requiredAppetizers;
        };
    }

    /** Returns whether this is the a-la-carte combo type. */
    public boolean isALaCarte() {
        return this == A_LA_CARTE;
    }

    /** Returns the combo type with database id or null if none match */
    public static ComboType fromId(int id) {
        for (ComboType type : values()) {
            if (type.comboId == id) {
                return type;
            }
        }
        return null;
    }
}
