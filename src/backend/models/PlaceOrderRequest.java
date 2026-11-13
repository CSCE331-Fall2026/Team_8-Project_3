package models;

import java.util.List;

public record PlaceOrderRequest (
    String paymentMethod,
    List<OrderLine> lines
    ) {
    public PlaceOrderRequest {
        lines = lines == null ? List.of() : List.copyOf(lines);
    }
}
