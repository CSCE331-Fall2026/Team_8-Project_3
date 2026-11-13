package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import models.MenuItemType;
import models.Order;
import models.OrderLine;
import models.PaymentType;

import javax.sql.DataSource;

public class OrderRepository {
    private static final String COLUMNS = "order_id, order_status::text AS order_status, "
            + "created_at::text AS created_at, updated_at::text AS updated_at, "
            + "payment_method::text AS payment_method, order_total";

    private final DataSource pool;

    public OrderRepository(DataSource pool) {
        this.pool = pool;
    }

    public Order placeOrder(PaymentType paymentType, List<OrderLine> lines) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int orderId = insertOrderRow(conn, paymentType);
                for (OrderLine line : lines) {
                    insertLine(conn, orderId, line);
                }
                Order order = writeOrderTotal(conn, orderId);
                updateIngredientStock(conn, orderId);
                conn.commit();
                return order;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
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


    // Helper functions used internally
    private int insertOrderRow(Connection conn, PaymentType paymentType) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO orders (payment_method) VALUES (?::payment_method) RETURNING order_id")) {
                stmt.setString(1, paymentType.dbValue());
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return rs.getInt("order_id");
        }
    }

    private void insertLine(Connection conn, int orderId, OrderLine line) throws SQLException {
        int orderDetailId;
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO order_details (order_id, combo_id, order_qty) VALUES (?, ?, ?) "
                        + "RETURNING order_detail_id")) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, line.comboId());
            stmt.setInt(3, line.quantity());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            orderDetailId = rs.getInt("order_detail_id");
        }

        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO combo_details (order_detail_id, menu_item_id, servings) VALUES (?, ?, 1)")) {
            for (int menuItemId : line.menuItemIds()) {
                stmt.setInt(1, orderDetailId);
                stmt.setInt(2, menuItemId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private Order writeOrderTotal(Connection conn, int orderId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE orders SET order_total = "
                        + "COALESCE((SELECT SUM(c.combo_price * od.order_qty) FROM order_details AS od "
                        + "JOIN combos AS c ON c.combo_id = od.combo_id WHERE od.order_id = ?), 0) "
                        + "+ COALESCE((SELECT SUM(mi.premium_charge * od.order_qty) FROM order_details AS od "
                        + "JOIN combo_details AS cd ON cd.order_detail_id = od.order_detail_id "
                        + "JOIN menu_items AS mi ON mi.menu_item_id = cd.menu_item_id "
                        + "WHERE od.order_id = ?), 0) "
                        + "WHERE order_id = ? RETURNING " + COLUMNS);
        stmt.setInt(1, orderId);
        stmt.setInt(2, orderId);
        stmt.setInt(3, orderId);
        return query(stmt).get(0);
    }

    private void updateIngredientStock(Connection conn, int orderId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE ingredients AS i SET quantity_in_stock = i.quantity_in_stock - u.used "
                        + "FROM (SELECT mid.ingredient_id, "
                        + "SUM(mid.ingredient_quantity * cd.servings * od.order_qty) AS used "
                        + "FROM order_details AS od "
                        + "JOIN combo_details AS cd ON cd.order_detail_id = od.order_detail_id "
                        + "JOIN menu_item_details AS mid ON mid.menu_item_id = cd.menu_item_id "
                        + "WHERE od.order_id = ? GROUP BY mid.ingredient_id) AS u "
                        + "WHERE i.ingredient_id = u.ingredient_id")) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
    }

    // Helper function for service
    public Map<Integer, MenuItemType> getActiveItemTypes(Collection<Integer> itemIds) throws SQLException {
        Map<Integer, MenuItemType> itemTypes = new HashMap<>();
        if (itemIds.isEmpty()) {
            return itemTypes;
        }
        try (Connection conn = pool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT menu_item_id, menu_item_type::text AS menu_item_type "
                             + "FROM menu_items WHERE active = TRUE AND menu_item_id = ANY(?)")) {
            stmt.setArray(1, conn.createArrayOf("integer", itemIds.toArray()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                itemTypes.put(rs.getInt("menu_item_id"), MenuItemType.fromDb(rs.getString("menu_item_type")));
            }
        }
        return itemTypes;
    }
}
