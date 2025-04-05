package com.foodorderingsystem.FoodOrderingApplication.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackDTO {
    private String message;

    @Min(1)
    @Max(5)
    private int stars;
}

