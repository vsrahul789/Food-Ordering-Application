package com.foodorderingsystem.FoodOrderingApplication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartDTO {
    private Long menuItemId;
    private int quantity;
}

