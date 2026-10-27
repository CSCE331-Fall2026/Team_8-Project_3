package services;

import repository.CashierRepository;

public class CashierService {
    private final CashierRepository cashierRepository;

    public CashierService(CashierRepository cashierRepository) {
        this.cashierRepository = cashierRepository;
    }

    public void placeOrder(double amount) {
        return;
    }

    public models.ComboType getOrder(int id) {
        return cashierRepository.getOrderById(id);
    }
}

// TODO: expand Cashier functionality alongside controllers
// Services does not know about http and deals with java types + our own models
// Right now this just has WIP proof of concept code