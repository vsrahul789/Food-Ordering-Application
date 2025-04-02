package com.foodorderingsystem.FoodOrderingApplication.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StripeRequest {
    private Long amount;
    private Long quantity;
    private String name;
    private String currency;
}
