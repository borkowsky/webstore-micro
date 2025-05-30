package net.rewerk.webstore.events.repository;

import net.rewerk.webstore.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Event service JPA repository
 *
 * @author rewerk
 */

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {
}
