package com.foodorderingsystem.FoodOrderingApplication.service;

import com.foodorderingsystem.FoodOrderingApplication.dto.StripeRequest;
import com.foodorderingsystem.FoodOrderingApplication.dto.StripeResponse;

public interface StripeService {
    StripeResponse checkoutProducts(StripeRequest stripeRequest) throws Exception;
}
