package models;

public record Order(
        Integer orderId,
        String orderStatus,
        String createdAt,
        String updatedAt,
        String paymentMethod,
        Double orderTotal) {
}
