package services;

import java.sql.SQLException;
import java.util.*;

import models.*;
import repository.UsersRepository;

public class UsersService {
    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User createUser(CreateUserRequest request) throws SQLException {
        requireText(request.firstName(), "First name", 50);
        requireText(request.lastName(), "Last name", 50);
        requireText(request.email(), "Email", 50);
        if (!request.email().contains("@")) {
            throw new IllegalArgumentException("Email is not valid");
        }
        if (request.phoneNumber() != null && request.phoneNumber().length() > 16) {
            throw new IllegalArgumentException("Phone number must be 16 characters or less");
        }

        UserRole userRole = UserRole.fromDb(request.userRole());
        if (userRole == null) {
            throw new IllegalArgumentException("Invalid or missing user role: " + request.userRole());
        }

        try {
            return usersRepository.createUser(userRole, request);
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalArgumentException("Email is already in use");
            }
            throw e;
        }
    }

    public User getUserById(int id) throws SQLException {
        return usersRepository.getUserById(id);
    }

    public User updateUser(int id, CreateUserRequest request) throws SQLException {
        requireText(request.firstName(), "First name", 50);
        requireText(request.lastName(), "Last name", 50);
        requireText(request.email(), "Email", 50);
        if (!request.email().contains("@")) {
            throw new IllegalArgumentException("Email is not valid");
        }
        if (request.phoneNumber() != null && request.phoneNumber().length() > 16) {
            throw new IllegalArgumentException("Phone number must be 16 characters or less");
        }

        UserRole userRole = UserRole.fromDb(request.userRole());
        if (userRole == null) {
            throw new IllegalArgumentException("Invalid or missing user role: " + request.userRole());
        }

        try {
            return usersRepository.updateUser(id, userRole, request);
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalArgumentException("Email is already in use");
            }
            throw e;
        }
    }

    public boolean deleteUser(int id) throws SQLException {
        return usersRepository.softDeleteUser(id);
    }

    // Helpers
    private void requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must be " + maxLength + " characters or less");
        }
    }
}