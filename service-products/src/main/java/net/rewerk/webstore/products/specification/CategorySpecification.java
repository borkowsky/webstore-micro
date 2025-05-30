package net.rewerk.webstore.products.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.NonNull;
import net.rewerk.webstore.dto.request.category.CategorySearchDto;
import net.rewerk.webstore.entity.Category;
import net.rewerk.webstore.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for formation Category entity JPA specification
 *
 * @author rewerk
 */

public abstract class CategorySpecification {

    /**
     * Get JPA specification by search DTO and user to provide Category entity search
     *
     * @param categorySearchDto DTO with search parameters
     * @param user              Authenticated user
     * @return JPA specification for Category entity
     */

    public static Specification<Category> getSpecification(@NonNull CategorySearchDto categorySearchDto,
                                                           User user) {
        return (root, _, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (categorySearchDto.getCategory_id() == null) {
                predicates.add(cb.isNull(root.get("categoryId")));
            } else if (categorySearchDto.getCategory_id() > 0) {
                predicates.add(cb.equal(root.get("categoryId"), categorySearchDto.getCategory_id()));
            }
            if (user == null || !List.of(User.Role.ROLE_ADMIN, User.Role.ROLE_SERVICE).contains(user.getRole())) {
                predicates.add(cb.equal(root.get("enabled"), true));
            } else {
                if (categorySearchDto.getEnabled() != null) {
                    predicates.add(cb.equal(root.get("enabled"), categorySearchDto.getEnabled()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
