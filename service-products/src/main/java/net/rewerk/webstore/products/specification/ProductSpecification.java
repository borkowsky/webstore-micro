package net.rewerk.webstore.products.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.NonNull;
import net.rewerk.webstore.dto.request.product.ProductSearchDto;
import net.rewerk.webstore.entity.Product;
import net.rewerk.webstore.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for formation Product entity JPA specification
 *
 * @author rewerk
 */

public abstract class ProductSpecification {

    /**
     * Get JPA specification by search DTO and user to provide Product entity search
     *
     * @param productSearchDto DTO with search parameters
     * @param user             Authenticated user
     * @return JPA specification for Product entity
     */

    public static Specification<Product> getSpecification(@NonNull ProductSearchDto productSearchDto,
                                                          User user) {
        return (root, _, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (productSearchDto.getCategory_id() == null) {
                predicates.add(cb.isNull(root.get("category")));
            } else if (productSearchDto.getCategory_id() > 0) {
                predicates.add(cb.equal(root.get("category").get("id"), productSearchDto.getCategory_id()));
            }
            if (productSearchDto.getBrand_id() != null && productSearchDto.getBrand_id() > 0) {
                predicates.add(cb.equal(root.get("brand").get("id"), productSearchDto.getBrand_id()));
            }
            if (user == null || !List.of(User.Role.ROLE_ADMIN, User.Role.ROLE_SERVICE).contains(user.getRole())) {
                predicates.add(cb.equal(root.get("enabled"), true));
                predicates.add(cb.greaterThan(root.get("balance"), 0));
                predicates.add(cb.equal(root.get("category").get("enabled"), true));
            } else {
                if (productSearchDto.getAvailable() != null) {
                    predicates.add(cb.equal(root.get("enabled"), productSearchDto.getAvailable()));
                    predicates.add(cb.equal(root.get("category").get("enabled"), productSearchDto.getAvailable()));
                    if (productSearchDto.getAvailable()) {
                        predicates.add(cb.greaterThan(root.get("balance"), 0));
                    }
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
