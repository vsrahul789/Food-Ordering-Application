package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.dto.StripeRequest;
import com.foodorderingsystem.FoodOrderingApplication.dto.StripeResponse;
import com.foodorderingsystem.FoodOrderingApplication.service.StripeService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Override
    public StripeResponse checkoutProducts(StripeRequest stripeRequest) throws Exception {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new Exception("Stripe secret key is not configured.");
        }
        Stripe.apiKey = secretKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(stripeRequest.getName())
                .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(stripeRequest.getCurrency())
                .setUnitAmount(stripeRequest.getAmount())
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                .setPriceData(priceData)
                .setQuantity(stripeRequest.getQuantity())
                .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                .addLineItem(lineItem)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel")
                .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return StripeResponse.builder()
                .sessionId(session.getId())
                .message("Success")
                .status("Success")
                .sessionUrl(session.getUrl())
                .build();
    }
}
