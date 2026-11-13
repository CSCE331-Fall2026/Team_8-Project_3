package services;

import java.sql.SQLException;
import java.util.*;

import models.*;
import repository.OrderRepository;

public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order placeOrder(PlaceOrderRequest request) throws SQLException {
        PaymentType paymentType = PaymentType.fromDb(request.paymentMethod());
        if (paymentType == null) {
            throw new IllegalArgumentException("Invalid PaymentMethod: " + request.paymentMethod());
        }
        if (request.lines().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one line");
        }

        Set<Integer> itemIds = new HashSet<>();
        for (OrderLine line : request.lines()) {
            itemIds.addAll(line.menuItemIds());
        }
        Map<Integer, MenuItemType> itemTypes = orderRepository.getActiveItemTypes(itemIds);
        for (OrderLine line : request.lines()) {
            validateLine(line, itemTypes);
        }
        return orderRepository.placeOrder(paymentType, request.lines());
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

    //Helpers
    private void validateLine(OrderLine line, Map<Integer, MenuItemType> itemTypes) {
        if (line.comboId() == null) {
            throw new IllegalArgumentException("Order line must have a comboId");
        }
        ComboType combo = ComboType.fromId(line.comboId());
        if (combo == null) {
            throw new IllegalArgumentException("Unknown comboId: " + line.comboId());
        }
        if (line.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Map<MenuItemType, Integer> counts = new EnumMap<>(MenuItemType.class);
        for (Integer id : line.menuItemIds()) {
            MenuItemType type = itemTypes.get(id);
            if (type == null) {
                throw new IllegalArgumentException("ItemId does not exist or not available: " + id);
            }
            counts.merge(type, 1, Integer::sum);
        }

        if (combo.isALaCarte()) {
            if (line.menuItemIds().size() != 1 || counts.containsKey(MenuItemType.DRINK)) {
                throw new IllegalArgumentException("A La Carte must be one side, entree, or appetizer (not a drink)");
            }
            return;
        }
        for (MenuItemType type : MenuItemType.values()) {
            int required = combo.limitFor(type);
            if (counts.getOrDefault(type, 0) != required) {
                throw new IllegalArgumentException(
                        combo.displayName() + " requires " + required + " " + type.dbValue() + "(s)");
            }
        }
    }
}

// Services does not know about http and deals with java types + our own models
