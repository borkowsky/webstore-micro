package net.rewerk.webstore.reviews.specification;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.NotNull;
import net.rewerk.webstore.dto.request.review.ReviewSearchDto;
import net.rewerk.webstore.entity.Review;
import net.rewerk.webstore.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for formation Review entity JPA specification
 *
 * @author rewerk
 */

public abstract class ReviewSpecification {

    /**
     * Get JPA specification by search DTO and user to provide Review entity search
     *
     * @param searchDto DTO with search parameters
     * @param user      Authenticated user
     * @return Review entity JPA specification
     */

    public static Specification<Review> getSpecification(
            @NotNull ReviewSearchDto searchDto,
            User user
    ) {
        return (root, _, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (searchDto.getUser_id() != null) {
                if (List.of(User.Role.ROLE_ADMIN, User.Role.ROLE_SERVICE).contains(user.getRole())) {
                    predicates.add(cb.equal(root.get("userId"), searchDto.getUser_id()));
                } else {
                    predicates.add(cb.equal(root.get("userId"), user.getId()));
                }
            }
            if (searchDto.getProduct_id() != null) {
                predicates.add(cb.equal(root.get("productId"), searchDto.getProduct_id()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
