package net.rewerk.webstore.orders.repository;

import net.rewerk.webstore.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer>, JpaSpecificationExecutor<Payment> {
    Optional<Payment> findByIdAndUserId(Integer id, UUID userId);

    List<Payment> findByCreatedAtGreaterThan(Date createdAt);

    @Query(value = "select sum(p.sum) from payments p " +
            "where p.user_id = :userId and p.updated_at > :date and p.status = 'APPROVED'::payments_status",
            nativeQuery = true)
    Double getTotalSumByCreatedAtGreaterThanAndUserId(Date date, UUID userId);

    @Query(value = "select sum(p.sum) from payments p " +
            "where p.user_id = :userId and p.status = 'APPROVED'::payments_status", nativeQuery = true)
    Double getTotalSumByUserId(UUID userId);
}
