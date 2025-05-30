package net.rewerk.webstore.orders.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.NonNull;
import net.rewerk.webstore.entity.Order;
import net.rewerk.webstore.entity.User;
import net.rewerk.webstore.dto.request.order.OrderSearchDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for formation Order entity JPA specification
 *
 * @author rewerk
 */

public abstract class OrderSpecification {

    /**
     * Get JPA specification to provide Order entity search
     *
     * @param user           Authenticated user
     * @param orderSearchDto DTO with search parameters
     * @return JPA specification for Order entity
     */

    public static Specification<Order> getSpecification(@NonNull User user,
                                                        @NonNull OrderSearchDto orderSearchDto
    ) {
        return (root, _, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (User.Role.ROLE_USER.equals(user.getRole())) {
                predicates.add(cb.equal(root.get("userId"), user.getId()));
            } else if (orderSearchDto.getUser_id() != null) {
                predicates.add(cb.equal(root.get("userId"), orderSearchDto.getUser_id()));
            }
            if (orderSearchDto.getType() != null) {
                if ("active".equals(orderSearchDto.getType())) {
                    predicates.add(
                            cb.not(root.get("status").in(List.of(
                                    Order.Status.RECEIVED
                            )))
                    );
                } else {
                    predicates.add(root.get("status").in(List.of(
                            Order.Status.RECEIVED
                    )));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
