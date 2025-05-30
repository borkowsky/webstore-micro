package net.rewerk.users.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.NonNull;
import net.rewerk.webstore.dto.request.basket.BasketSearchDto;
import net.rewerk.webstore.entity.Basket;
import net.rewerk.webstore.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for formation Address entity JPA specification
 *
 * @author rewerk
 */

public abstract class BasketSpecification {

    /**
     * Get JPA specification by search DTO and user to provide Basket entity search
     *
     * @param searchDto DTO with search parameters
     * @param user      Authenticated user
     * @return Basket entity JPA specification
     */

    public static Specification<Basket> getSpecification(
            @NonNull BasketSearchDto searchDto,
            @NonNull User user
    ) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Utils.addUserIdConstraint(root, cb, cq, predicates, user, searchDto.getUser_id());
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Get JPA specification by user
     *
     * @param user Authenticated user
     * @return Basket entity JPA specification
     */

    public static Specification<Basket> getSpecification(User user) {
        return (root, _, cb) -> cb.equal(root.get("userId"), user.getId());
    }
}
