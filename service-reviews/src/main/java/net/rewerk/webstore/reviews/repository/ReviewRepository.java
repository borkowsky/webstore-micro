package net.rewerk.webstore.reviews.repository;

import net.rewerk.webstore.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Review entity JPA repository
 *
 * @author rewerk
 */

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>, JpaSpecificationExecutor<Review> {
    boolean existsByOrderIdAndProductIdAndUserId(Integer orderId, Integer productId, UUID userId);

    @Query(
            value = "select r.images from reviews r where r.product_id = :productId limit 15",
            nativeQuery = true
    )
    List<List<String>> findAllImagesByProductId(Integer productId);

    @Query(
            value = "select r.rating from reviews r where r.product_id = :productId order by id desc limit 50",
            nativeQuery = true
    )
    List<Integer> findLastRatingsByProductId(Integer productId);

    @Query(
            value = "select avg(r.rating) from " +
                    "(select rating from reviews where product_id = :productId order by id desc limit 50) as r",
            nativeQuery = true
    )
    Double findActualAverageRatingByProductId(Integer productId);

    long countByCreatedAtGreaterThan(Date createdAtIsGreaterThan);
}
