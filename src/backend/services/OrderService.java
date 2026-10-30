package services;

import java.sql.SQLException;
import java.util.List;

import models.Order;
import repository.OrderRepository;

public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order placeOrder(Order order) throws SQLException {
        return orderRepository.insertOrder(order);
    }

    public Order getOrder(int id) throws SQLException {
        return orderRepository.getOrderById(id);
    }

    public List<Order> getRecentOrders(int n) throws SQLException {
        return orderRepository.getRecentOrders(n);
    }

    public boolean updateOrder(int id, Order order) throws SQLException {
        return orderRepository.updateOrder(id, order);
    }

    public boolean deleteOrder(int id) throws SQLException {
        return orderRepository.deleteOrder(id);
    }
}

// TODO: expand Cashier functionality alongside controllers
// Services does not know about http and deals with java types + our own models
