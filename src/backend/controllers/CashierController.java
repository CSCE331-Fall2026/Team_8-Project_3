package controllers;

import io.javalin.http.Context;

import services.CashierService;

public class CashierController {
    private final CashierService cashierService;

    public CashierController(CashierService cashierService, CashierService cashierService1) {
        this.cashierService = cashierService;
    }

    public void placeOrder(Context ctx) {
        double amount = Double.parseDouble(ctx.pathParam("amount"));

        cashierService.placeOrder(amount);
        ctx.status(201).result("Order placed");
    }
}

// TODO: Implement cashier functionality
// Services knows about http and returns status codes and unpacks JSON from request body