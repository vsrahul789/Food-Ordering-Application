package com.foodorderingsystem.FoodOrderingApplication.exception;

public class StripeException extends RuntimeException {
    public StripeException(String message) {
        super(message);
    }
}
