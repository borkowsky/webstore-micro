package net.rewerk.users.repository;

import lombok.NonNull;
import net.rewerk.webstore.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Basket entity JPA repository
 *
 * @author rewerk
 */

@Repository
public interface BasketRepository extends JpaRepository<Basket, Integer>, JpaSpecificationExecutor<Basket> {
    boolean existsByProductIdAndUserId(Integer productId, UUID userId);

    List<Basket> findAllByIdInAndUserId(Collection<Integer> ids, UUID userId);

    Optional<Basket> findByIdAndUserId(@NonNull Integer id, UUID userId);

    List<Basket> findAllByProductIdInAndUserId(Collection<Integer> productIds, UUID userId);
}
