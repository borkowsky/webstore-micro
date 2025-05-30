package net.rewerk.webstore.events.controller;

import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.event.EventSearchDto;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.event.EventResponseDto;
import net.rewerk.webstore.events.service.entity.EventService;
import net.rewerk.webstore.events.specification.EventSpecification;
import net.rewerk.webstore.utility.RequestUtils;
import net.rewerk.webstore.utility.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for events service
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@RestController
public class EventsController {
    private final EventService eventService;

    /**
     * GET endpoint for retrieve events
     *
     * @param searchDto Search DTO with search parameters
     * @return Response with status 200 OK and paginated payload with Event entity response DTO
     */
    @GetMapping
    public ResponseEntity<PaginatedPayloadResponseDto<EventResponseDto>> findAllEvents(
            EventSearchDto searchDto
    ) {
        return ResponseUtils.createPaginatedResponse(eventService.findAll(
                EventSpecification.getSpecification(searchDto),
                RequestUtils.getSortAndPageRequest(searchDto)
        ));
    }
}
