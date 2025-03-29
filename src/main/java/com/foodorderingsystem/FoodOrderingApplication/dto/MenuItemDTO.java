package com.foodorderingsystem.FoodOrderingApplication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemDTO {
    private String name;
    private String description;
    private double price;
}