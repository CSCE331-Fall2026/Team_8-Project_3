package models;

public enum PaymentType {
    CREDIT_CARD("credit_card"),
    DEBIT_CARD("debit_card"),
    CASH("cash"),
    GIFT_CARD("gift_card"),
    MEAL_PASS("meal_pass");

    private final String dbValue;
    PaymentType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static PaymentType fromDb(String value) {
        for (PaymentType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}