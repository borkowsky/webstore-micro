package net.rewerk.webstore.products.specification;

import lombok.NonNull;
import net.rewerk.webstore.dto.request.stats.StatsSearchDto;
import net.rewerk.webstore.entity.Stats;
import org.springframework.data.jpa.domain.Specification;

/**
 * Abstract class for formation Stats entity JPA specification
 *
 * @author rewerk
 */

public abstract class StatsSpecification {

    /**
     * Get JPA specification by search DTO to provide Stats entity search
     *
     * @param dto DTO with search parameters
     * @return JPA specification for Stats entity
     */

    public static Specification<Stats> getSpecification(@NonNull StatsSearchDto dto) {
        return (root, cq, cb) -> cb.and();
    }
}
