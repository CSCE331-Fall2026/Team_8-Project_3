package models;

public record User (
    Integer userId,
    String firstName,
    String lastName,
    String firebaseId,
    String email,
    String phoneNumber,
    UserRole userRole,
    String createdAt,
    String updatedAt,
    Integer rewardsPoints
    ) {}
