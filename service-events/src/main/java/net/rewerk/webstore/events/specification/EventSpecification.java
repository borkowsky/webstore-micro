package net.rewerk.webstore.events.specification;

import jakarta.validation.constraints.NotNull;
import net.rewerk.webstore.dto.request.SortedRequestParamsDto;
import net.rewerk.webstore.dto.request.event.EventSearchDto;
import net.rewerk.webstore.entity.Event;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification class for Event JPA entity
 *
 * @author rewerk
 */
public abstract class EventSpecification extends SortedRequestParamsDto {
    /**
     * Get Specification for search requests
     *
     * @param searchDto Search DTO
     * @return Specification for Event entity
     */
    public static Specification<Event> getSpecification(@NotNull EventSearchDto searchDto) {
        return (_, _, cb) -> cb.and();
    }
}
