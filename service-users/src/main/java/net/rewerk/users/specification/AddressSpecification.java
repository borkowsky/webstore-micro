package net.rewerk.users.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.NonNull;
import net.rewerk.webstore.dto.request.address.AddressSearchDto;
import net.rewerk.webstore.entity.Address;
import net.rewerk.webstore.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for formation Address entity JPA specification
 *
 * @author rewerk
 */

public abstract class AddressSpecification {

    /**
     * Get JPA specification by search DTO and user to provide Address entity search
     *
     * @param user             Authenticated user
     * @param addressSearchDto DTO with search parameters
     * @return Address entity JPA specification
     */

    public static Specification<Address> getSpecification(@NonNull User user,
                                                          @NonNull AddressSearchDto addressSearchDto) {
        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Utils.addUserIdConstraint(root, cb, cq, predicates, user, addressSearchDto.getUser_id());
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
