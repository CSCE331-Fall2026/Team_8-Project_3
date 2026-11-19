package models;

import java.util.List;

public record PlaceOrderRequest (
    Integer customerId,
    Integer cashierId,
    String channel,
    String paymentMethod,
    List<OrderLine> lines
    ) {
    public PlaceOrderRequest {
        lines = lines == null ? List.of() : List.copyOf(lines);
    }
}
