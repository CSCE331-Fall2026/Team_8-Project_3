package models;

public record CreateUserRequest (
        String firstName,
        String lastName,
        String phoneNumber,
        String userRole
) {}