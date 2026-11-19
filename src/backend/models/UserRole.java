package models;

import io.javalin.security.RouteRole;

public enum UserRole implements RouteRole {
    MANAGER("manager"),
    CASHIER("cashier"),
    CUSTOMER("customer"),
    KITCHEN("kitchen");

    private final String dbValue;
    UserRole(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static UserRole fromDb(String value) {
        for (UserRole role : values()) {
            if (role.dbValue.equalsIgnoreCase(value)) {
                return role;
            }
        }
        return null;
    }
}