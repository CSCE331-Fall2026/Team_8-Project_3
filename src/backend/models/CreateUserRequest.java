package models;

public record CreateUserRequest (
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String userRole
) {}