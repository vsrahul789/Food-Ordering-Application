package com.foodorderingsystem.FoodOrderingApplication.exception;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }
}