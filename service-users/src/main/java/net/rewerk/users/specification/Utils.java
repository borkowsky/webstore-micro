package net.rewerk.users.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import net.rewerk.webstore.entity.User;

import java.util.List;
import java.util.UUID;

/**
 * Utility class for provide common methods to form JPA specifications
 *
 * @author rewerk
 */

public abstract class Utils {

    /**
     * Add user constraints for JPA specification
     *
     * @param root       Root type in the Criteria API from clause
     * @param cb         Criteria builder from Criteria API
     * @param cq         Criteria query from Criteria API
     * @param predicates List of predicates to add constraints
     * @param user       Authenticated user
     * @param userId     Requested user identifier parameter
     */

    public static void addUserIdConstraint(@NonNull Root<?> root,
                                           @NonNull CriteriaBuilder cb,
                                           CriteriaQuery<?> cq,
                                           @NonNull List<Predicate> predicates,
                                           @NonNull User user,
                                           UUID userId) {
        if (userId != null) {
            if (List.of(User.Role.ROLE_ADMIN, User.Role.ROLE_SERVICE).contains(user.getRole())) {
                predicates.add(cb.equal(root.get("userId"), userId));
            } else {
                predicates.add(cb.equal(root.get("userId"), user.getId()));
            }
        } else {
            predicates.add(cb.equal(root.get("userId"), user.getId()));
        }
    }
}
