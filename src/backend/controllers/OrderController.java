package controllers;

import java.util.List;

import io.javalin.http.Context;

import models.Order;
import services.OrderService;

public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /orders
    public void placeOrder(Context ctx) {
        Order order = ctx.bodyAsClass(Order.class);

        Order created = orderService.placeOrder(order);
        ctx.status(201).json(created);
    }

    // GET /orders/{orderID}
    public void getOrder(Context ctx) {
        int orderID = ctx.pathParamAsClass("orderID", Integer.class).get();

        Order order = orderService.getOrder(orderID);
        if (order == null) {
            ctx.status(404).result("Order not found");
            return;
        }
        ctx.json(order);
    }

    // GET /orders?n=10
    public void getOrders(Context ctx) {
        int n = ctx.queryParamAsClass("n", Integer.class)
                .check(value -> value > 0, "n must be a positive number")
                .get();

        List<Order> orders = orderService.getRecentOrders(n);
        ctx.json(orders);
    }

    // PUT /orders/{orderID}
    public void updateOrder(Context ctx) {
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
    // public void deleteOrder(Context ctx) {
    //     int orderID = ctx.pathParamAsClass("orderID", Integer.class).get();

    //     boolean deleted = orderService.deleteOrder(orderID);
    //     if (!deleted) {
    //         ctx.status(404).result("Order not found");
    //         return;
    //     }
    //     ctx.status(204);
    // }
}

// TODO: Implement cashier functionality
// Services knows about http and returns status codes and unpacks JSON from request body
