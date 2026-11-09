package models;

public enum MenuItemType {
    SIDE("side"),
    ENTREE("entree"),
    APPETIZER("appetizer"),
    DRINK("drink");

    private final String dbValue;
    MenuItemType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static MenuItemType fromDb(String value) {
        for (MenuItemType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
