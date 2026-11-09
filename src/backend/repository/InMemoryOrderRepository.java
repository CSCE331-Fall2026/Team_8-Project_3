package repository;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Order;

// Stand-in for OrderRepository when no database is configured; data is lost on restart
public class InMemoryOrderRepository extends OrderRepository {
    // Same text format Postgres uses for timestamptz::text
    static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSx");

    private static final Set<String> ORDER_STATUSES = Set.of("paid", "canceled", "refunded");
    private static final Set<String> PAYMENT_METHODS =
            Set.of("credit_card", "debit_card", "cash", "gift_card", "meal_pass");

    private final Map<Integer, Order> orders = new LinkedHashMap<>();
    private int nextId = 1;

    public InMemoryOrderRepository() {
        super(null);
        OffsetDateTime start = OffsetDateTime.now().minusHours(3);
        seed("paid", "credit_card", 9.40, start);
        seed("paid", "cash", 11.20, start.plusMinutes(25));
        seed("refunded", "debit_card", 13.00, start.plusMinutes(50));
        seed("paid", "meal_pass", 9.40, start.plusMinutes(90));
        seed("canceled", "gift_card", 4.50, start.plusMinutes(120));
    }

    private void seed(String status, String payment, double total, OffsetDateTime createdAt) {
        String time = createdAt.format(TIMESTAMP);
        int id = nextId++;
        orders.put(id, new Order(id, status, time, time, payment, total));
    }

    @Override
    public synchronized Order insertOrder(Order order) throws SQLException {
        validate(order);
        String now = OffsetDateTime.now().format(TIMESTAMP);
        int id = nextId++;
        Order created = new Order(id,
                order.orderStatus() == null ? "paid" : order.orderStatus(),
                now, now,
                order.paymentMethod(),
                order.orderTotal() == null ? 0.0 : order.orderTotal());
        orders.put(id, created);
        return created;
    }

    @Override
    public synchronized Order getOrderById(int id) {
        return orders.get(id);
    }

    @Override
    public synchronized List<Order> getRecentOrders(int n) {
        return orders.values().stream()
                .sorted(Comparator.comparing(Order::createdAt).reversed())
                .limit(n)
                .toList();
    }

    @Override
    public synchronized boolean updateOrder(int id, Order order) throws SQLException {
        Order current = orders.get(id);
        if (current == null) {
            return false;
        }
        validate(order);
        orders.put(id, new Order(id,
                order.orderStatus() == null ? current.orderStatus() : order.orderStatus(),
                current.createdAt(),
                OffsetDateTime.now().format(TIMESTAMP),
                order.paymentMethod() == null ? current.paymentMethod() : order.paymentMethod(),
                order.orderTotal() == null ? current.orderTotal() : order.orderTotal()));
        return true;
    }

    @Override
    public synchronized boolean deleteOrder(int id) {
        return orders.remove(id) != null;
    }

    // Matches the database rejecting values outside the order_status and payment_method enums
    private static void validate(Order order) throws SQLException {
        if (order.orderStatus() != null && !ORDER_STATUSES.contains(order.orderStatus())) {
            throw new SQLException("invalid input value for enum order_status: \"" + order.orderStatus() + "\"", "22P02");
        }
        if (order.paymentMethod() != null && !PAYMENT_METHODS.contains(order.paymentMethod())) {
            throw new SQLException("invalid input value for enum payment_method: \"" + order.paymentMethod() + "\"", "22P02");
        }
    }
}
