package controllers;

import io.javalin.http.Context;

import services.OrderService;

public class OrderController {
    private final OrderService OrderService;

    public OrderController(OrderService OrderService, OrderService OrderService1) {
        this.OrderService = OrderService;
    }

    public void placeOrder(Context ctx) {
        double amount = Double.parseDouble(ctx.pathParam("amount"));

        OrderService.placeOrder(amount);
        ctx.status(201).result("Order placed");
    }
}

// TODO: Implement cashier functionality
// Services knows about http and returns status codes and unpacks JSON from request body