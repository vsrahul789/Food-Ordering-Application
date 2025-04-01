package com.foodorderingsystem.FoodOrderingApplication.repository;


import com.foodorderingsystem.FoodOrderingApplication.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderId(Long orderId);
}
