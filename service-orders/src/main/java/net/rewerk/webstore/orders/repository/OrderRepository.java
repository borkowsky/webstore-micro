package net.rewerk.webstore.orders.repository;

import net.rewerk.webstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByIdAndUserId(Integer id, UUID userId);

    Optional<Order> findByPaymentIdAndUserId(Integer paymentId, UUID userId);

    Long countByCreatedAtGreaterThan(Date createdAtIsGreaterThan);
}
