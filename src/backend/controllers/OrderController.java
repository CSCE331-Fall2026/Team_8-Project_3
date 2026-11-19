package controllers;

import java.sql.SQLException;
import java.util.List;

import io.javalin.http.Context;
import io.javalin.config.JavalinConfig;

import models.Order;
import models.PlaceOrderRequest;
import models.User;
import models.UserRole;
import services.OrderService;

public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public void registerRoutes(JavalinConfig config) {
        config.routes.post("/orders", this::placeOrder,
                UserRole.MANAGER, UserRole.CUSTOMER, UserRole.CASHIER);
        config.routes.get("/orders/{orderID}", this::getOrderByID, UserRole.values());
        config.routes.get("/orders", this::getOrders,
                UserRole.MANAGER, UserRole.KITCHEN, UserRole.CASHIER);
        config.routes.put("/orders/{orderID}", this::updateOrder, UserRole.MANAGER);
        config.routes.delete("/orders/{orderID}", this::deleteOrder, UserRole.MANAGER);
    }

    // POST /orders
    public void placeOrder(Context ctx) throws SQLException {
        User caller = ctx.attribute("user");
        PlaceOrderRequest body = ctx.bodyAsClass(PlaceOrderRequest.class);

        PlaceOrderRequest request;
        if (caller.userRole() == UserRole.CUSTOMER) {
            request = new PlaceOrderRequest(
                    caller.userId(), null, "online", body.paymentMethod(), body.lines());
        } else {
            request = new PlaceOrderRequest(
                    body.customerId(), caller.userId(), "in_store", body.paymentMethod(), body.lines());
        }

        try {
            Order createdOrder = orderService.placeOrder(request);
            ctx.status(201).json(createdOrder);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        }
    }

    // GET /orders/{orderID}
    public void getOrderByID(Context ctx) throws SQLException {
        User caller = ctx.attribute("user");
        int orderID = ctx.pathParamAsClass("orderID", Integer.class).get();

        Order order = orderService.getOrder(orderID);
        boolean isOwner = order != null && caller.userId().equals(order.customerId());
        if (order == null || (caller.userRole() == UserRole.CUSTOMER && !isOwner)) {
            ctx.status(404).result("Order not found");
            return;
        }
        ctx.json(order);
    }

    // GET /orders?n=10
    public void getOrders(Context ctx) throws SQLException {
        int n = ctx.queryParamAsClass("n", Integer.class)
                .check(value -> value > 0, "n must be a positive number")
                .get();

        List<Order> orders = orderService.getRecentOrders(n);
        ctx.json(orders);
    }

    // PUT /orders/{orderID}
    public void updateOrder(Context ctx) throws SQLException {
        int orderID = ctx.pathParamAsClass("orderID", Integer.class).get();
        Order order = ctx.bodyAsClass(Order.class);

        boolean updated = orderService.updateOrder(orderID, order);
        if (!updated) {
            ctx.status(404).result("Order not found");
            return;
        }
        ctx.status(200).result("Order updated");
    }

    // DELETE /orders/{orderID}
    public void deleteOrder(Context ctx) throws SQLException {
        int orderID = ctx.pathParamAsClass("orderID", Integer.class).get();

        boolean deleted = orderService.deleteOrder(orderID);
        if (!deleted) {
            ctx.status(404).result("Order not found");
            return;
        }
        ctx.status(204);
    }
}

