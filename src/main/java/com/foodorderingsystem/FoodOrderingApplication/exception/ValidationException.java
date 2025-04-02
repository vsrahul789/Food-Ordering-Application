package com.foodorderingsystem.FoodOrderingApplication.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}