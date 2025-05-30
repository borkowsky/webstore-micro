package net.rewerk.webstore.products.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.NonNull;
import net.rewerk.webstore.dto.request.brand.BrandSearchDto;
import net.rewerk.webstore.entity.Brand;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for formation Brand entity JPA specification
 *
 * @author rewerk
 */

public abstract class BrandSpecification {

    /**
     * Get JPA specification by search DTO to provide Brand entity search
     *
     * @param brandSearchDto DTO with search parameters
     * @return JPA specification for Brand entity
     */

    public static Specification<Brand> getSpecification(@NonNull BrandSearchDto brandSearchDto) {
        return (root, _, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (brandSearchDto.getName_query() != null) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("name")),
                                cb.lower(
                                        cb.literal("%" + brandSearchDto.getName_query() + "%")
                                )
                        )
                );
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
