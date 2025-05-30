package net.rewerk.webstore.orders.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.NonNull;
import net.rewerk.webstore.entity.Payment;
import net.rewerk.webstore.entity.User;
import net.rewerk.webstore.dto.request.payment.PaymentSearchDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for formation Payment JPA specification
 *
 * @author rewerk
 */

public abstract class PaymentSpecification {

    /**
     * Get JPA specification to provide Payment entity search
     *
     * @param paymentSearchDto Search DTO
     * @param user             Authenticated user
     * @return JPA specification
     */

    public static Specification<Payment> getSpecification(@NonNull PaymentSearchDto paymentSearchDto,
                                                          @NonNull User user) {
        return (root, _, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), user.getId()));
            if (paymentSearchDto.getStatus() != null) {
                if ("pending".equals(paymentSearchDto.getStatus())) {
                    predicates.add(cb.equal(root.get("status"), Payment.Status.CREATED.toString()));
                } else {
                    predicates.add(cb.notEqual(root.get("status"), Payment.Status.CREATED.toString()));
                }
            }
            if (List.of(User.Role.ROLE_ADMIN, User.Role.ROLE_SERVICE).contains(user.getRole())) {
                if (paymentSearchDto.getUser_id() != null) {
                    predicates.add(cb.equal(root.get("userId"), paymentSearchDto.getUser_id()));
                }
            } else {
                predicates.add(cb.equal(root.get("userId"), user.getId()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
