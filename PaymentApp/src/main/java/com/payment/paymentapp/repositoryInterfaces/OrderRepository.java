package com.payment.paymentapp.repositoryInterfaces;

import com.payment.paymentapp.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Integer> {
}
