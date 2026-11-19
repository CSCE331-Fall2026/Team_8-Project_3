package models;

public enum OrderChannel {
    IN_STORE("in_store"),
    ONLINE("online");

    private final String dbValue;
    OrderChannel(String dbValue) { this.dbValue = dbValue; }

    public String dbValue() { return dbValue; }

    public static OrderChannel fromDb(String value) {
        for (OrderChannel channel : values()) {
            if (channel.dbValue.equalsIgnoreCase(value)) {
                return channel;
            }
        }
        return null;
    }
}