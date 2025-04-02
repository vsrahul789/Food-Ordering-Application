package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.dto.StripeRequest;
import com.foodorderingsystem.FoodOrderingApplication.dto.StripeResponse;
import com.foodorderingsystem.FoodOrderingApplication.exception.StripeException;
import com.foodorderingsystem.FoodOrderingApplication.service.StripeService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public StripeResponse checkoutProducts(StripeRequest stripeRequest) throws Exception {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            log.error("Stripe secret key is not configured.");
            throw new StripeException("Stripe secret key is not configured.");
        }

        try {
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
                    .setSuccessUrl("http://localhost:8082/success")
                    .setCancelUrl("http://localhost:8082/cancel")
                    .build();

            Session session = Session.create(params);

            log.info("Stripe payment session created successfully. Session ID: {}", session.getId());
            return StripeResponse.builder()
                    .sessionId(session.getId())
                    .message("Success")
                    .status("Success")
                    .sessionUrl(session.getUrl())
                    .build();
        } catch (StripeException e) {
            log.error("Stripe payment processing failed: {}", e.getMessage());
            throw new StripeException("Stripe payment failed: " + e.getMessage());
        }
    }
}
