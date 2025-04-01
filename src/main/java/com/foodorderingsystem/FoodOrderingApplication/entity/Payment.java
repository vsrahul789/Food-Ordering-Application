package com.foodorderingsystem.FoodOrderingApplication.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.foodorderingsystem.FoodOrderingApplication.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String transactionId;

    private Double amount;

    private LocalDateTime timestamp;
}

