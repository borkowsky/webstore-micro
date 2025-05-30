package net.rewerk.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import net.rewerk.webstore.entity.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Address entity JPA repository
 *
 * @author rewerk
 */

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer>, JpaSpecificationExecutor<Address> {
    Optional<Address> findByIdAndUserId(Integer id, UUID userId);

    List<Address> findByUserId(UUID userId);
}
