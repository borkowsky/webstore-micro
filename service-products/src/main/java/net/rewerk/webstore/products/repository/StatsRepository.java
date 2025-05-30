package net.rewerk.webstore.products.repository;

import net.rewerk.webstore.entity.Stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Stats entity JPA repository
 *
 * @author rewerk
 */

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long>, JpaSpecificationExecutor<Stats> {
}
