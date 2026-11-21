package models;

public record Order(
        Integer orderId,
        Integer customerId,
        Integer cashierId,
        String orderStatus,
        String orderChannel,
        String createdAt,
        String updatedAt,
        String paymentMethod,
        Double orderTotal,
        Integer rewardsEarned,
        Integer rewardsSpent) {
}
