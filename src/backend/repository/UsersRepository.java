package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.CreateUserRequest;
import models.User;
import models.UserRole;

import javax.sql.DataSource;

public class UsersRepository {
    private static final String COLUMNS = "user_id, firebase_id, first_name, last_name, email, phone_number, "
            + "user_role::text AS user_role, created_at::text AS created_at, "
            + "updated_at::text AS updated_at, rewards_points";

    private final DataSource pool;

    public UsersRepository(DataSource pool) {
        this.pool = pool;
    }

    public User createUser(UserRole userRole, CreateUserRequest request, String firebaseId, String email)
            throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO users (firebase_id, first_name, last_name, email, phone_number, user_role) "
                            + "VALUES (?, ?, ?, ?, ?, ?::user_role) RETURNING " + COLUMNS);
            stmt.setString(1, firebaseId);
            stmt.setString(2, request.firstName());
            stmt.setString(3, request.lastName());
            stmt.setString(4, email);
            stmt.setString(5, request.phoneNumber());
            stmt.setString(6, userRole.dbValue());
            return query(stmt).get(0);
        }
    }

    public User getUserById(int id) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT " + COLUMNS + " FROM users WHERE user_id = ?" +
                    " AND active = TRUE");
            stmt.setInt(1, id);
            List<User> users = query(stmt);
            return users.isEmpty() ? null : users.get(0);
        }
    }

    public User getUserByFirebaseId(String firebaseId) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT " + COLUMNS + " FROM users WHERE firebase_id = ? AND active = TRUE");
            stmt.setString(1, firebaseId);
            List<User> users = query(stmt);
            return users.isEmpty() ? null : users.get(0);
        }
    }

    public User updateUser(int id, UserRole userRole, CreateUserRequest request) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE users SET first_name = ?, last_name = ?, phone_number = ?, "
                            + "user_role = ?::user_role, updated_at = CURRENT_TIMESTAMP "
                            + "WHERE user_id = ? AND active = TRUE RETURNING " + COLUMNS);
            stmt.setString(1, request.firstName());
            stmt.setString(2, request.lastName());
            stmt.setString(3, request.phoneNumber());
            stmt.setString(4, userRole.dbValue());
            stmt.setInt(5, id);
            List<User> users = query(stmt);
            return users.isEmpty() ? null : users.get(0);
        }
    }

    public boolean softDeleteUser(int id) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE users SET active = FALSE, updated_at = CURRENT_TIMESTAMP "
                            + "WHERE user_id = ? AND active = TRUE");
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Internal helper
    private List<User> query(PreparedStatement stmt) throws SQLException {
        List<User> users = new ArrayList<>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            users.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("firebase_id"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    UserRole.fromDb(rs.getString("user_role")),
                    rs.getString("created_at"),
                    rs.getString("updated_at"),
                    rs.getInt("rewards_points")));
        }
        stmt.close();
        return users;
    }
}

