package com.foodorderingsystem.FoodOrderingApplication.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StripeResponse {
    private String sessionId;
    private String message;
    private String status;
    private String sessionUrl;
}
