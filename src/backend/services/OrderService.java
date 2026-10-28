package services;

import repository.OrderRepository;

public class OrderService {
    private final OrderRepository OrderRepository;

    public OrderService(OrderRepository OrderRepository) {
        this.OrderRepository = OrderRepository;
    }

    public void placeOrder(double amount) {
        return;
    }

    public models.ComboType getOrder(int id) {
        return OrderRepository.getOrderById(id);
    }
}

// TODO: expand Cashier functionality alongside controllers
// Services does not know about http and deals with java types + our own models
// Right now this just has WIP proof of concept code