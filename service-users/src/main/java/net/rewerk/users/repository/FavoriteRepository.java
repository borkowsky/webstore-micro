package net.rewerk.users.repository;

import net.rewerk.webstore.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Favorite entity JPA repository
 *
 * @author rewerk
 */

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer>, JpaSpecificationExecutor<Favorite> {
    boolean existsByUserIdAndProductId(UUID userId, Integer productId);

    Optional<Favorite> findByIdAndUserId(Integer id, UUID userId);
}
