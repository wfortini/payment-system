package com.wfortini.pspemulator.repository;

import com.wfortini.pspemulator.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
