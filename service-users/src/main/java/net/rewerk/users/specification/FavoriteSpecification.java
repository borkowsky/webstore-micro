package net.rewerk.users.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.NonNull;
import net.rewerk.webstore.dto.request.favorite.FavoriteSearchDto;
import net.rewerk.webstore.entity.Favorite;
import net.rewerk.webstore.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for formation Favorite entity JPA specification
 *
 * @author rewerk
 */

public abstract class FavoriteSpecification {

    /**
     * Get JPA specification by search DTO and user to provide Favorite entity search
     *
     * @param searchDto DTO with search parameters
     * @param user      Authenticated user
     * @return Favorite entity JPA specification
     */

    public static Specification<Favorite> getSpecification(@NonNull FavoriteSearchDto searchDto,
                                                           @NonNull User user) {
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
     * @return Favorite entity JPA specification
     */

    public static Specification<Favorite> getSpecification(@NonNull User user) {
        return (root, _, cb) -> cb.equal(root.get("userId"), user.getId());
    }
}
