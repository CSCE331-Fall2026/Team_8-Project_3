package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import models.Order;

import javax.sql.DataSource;

public class OrderRepository {
    private static final String COLUMNS = "order_id, order_status::text AS order_status, "
            + "created_at::text AS created_at, updated_at::text AS updated_at, "
            + "payment_method::text AS payment_method, order_total";

    private final DataSource pool;

    public OrderRepository(DataSource pool) {
        this.pool = pool;
    }

    public Order insertOrder(Order order) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO orders (order_status, payment_method, order_total) "
                            + "VALUES (COALESCE(?::order_status, 'paid'), ?::payment_method, COALESCE(?, 0)) "
                            + "RETURNING " + COLUMNS);
            stmt.setString(1, order.orderStatus());
            stmt.setString(2, order.paymentMethod());
            stmt.setObject(3, order.orderTotal(), Types.DOUBLE);
            return query(stmt).get(0);
        }
    }

    public Order getOrderById(int id) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT " + COLUMNS + " FROM orders WHERE order_id = ?");
            stmt.setInt(1, id);
            List<Order> orders = query(stmt);
            return orders.isEmpty() ? null : orders.get(0);
        }
    }

    public List<Order> getRecentOrders(int n) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT " + COLUMNS + " FROM orders ORDER BY created_at DESC LIMIT ?");
            stmt.setInt(1, n);
            return query(stmt);
        }
    }

    // Fields left null in the request keep their current value
    public boolean updateOrder(int id, Order order) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE orders SET order_status = COALESCE(?::order_status, order_status), "
                            + "payment_method = COALESCE(?::payment_method, payment_method), "
                            + "order_total = COALESCE(?, order_total), updated_at = CURRENT_TIMESTAMP "
                            + "WHERE order_id = ?");
            stmt.setString(1, order.orderStatus());
            stmt.setString(2, order.paymentMethod());
            stmt.setObject(3, order.orderTotal(), Types.DOUBLE);
            stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // order_details and combo_details reference the order, so they are deleted in the same statement
    public boolean deleteOrder(int id) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "WITH details AS (DELETE FROM order_details WHERE order_id = ? RETURNING order_detail_id), "
                            + "combos AS (DELETE FROM combo_details WHERE order_detail_id IN "
                            + "(SELECT order_detail_id FROM details)) "
                            + "DELETE FROM orders WHERE order_id = ?");
            stmt.setInt(1, id);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private List<Order> query(PreparedStatement stmt) throws SQLException {
        List<Order> orders = new ArrayList<>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            orders.add(new Order(
                    rs.getInt("order_id"),
                    rs.getString("order_status"),
                    rs.getString("created_at"),
                    rs.getString("updated_at"),
                    rs.getString("payment_method"),
                    rs.getDouble("order_total")));
        }
        stmt.close();
        return orders;
    }
}
