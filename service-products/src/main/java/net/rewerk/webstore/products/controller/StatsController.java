package net.rewerk.webstore.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.stats.StatsSearchDto;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.stats.StatsResponseDto;
import net.rewerk.webstore.products.service.entity.StatsService;
import net.rewerk.webstore.products.specification.StatsSpecification;
import net.rewerk.webstore.utility.RequestUtils;
import net.rewerk.webstore.utility.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Stats service
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {
    private final StatsService statsService;

    /**
     * GET endpoint for retrieve stats by search criteria
     *
     * @param searchDto DTO with search parameters
     * @return Response with status 200 OK and payload with page of StatsResponseDto
     */

    @GetMapping
    public ResponseEntity<PaginatedPayloadResponseDto<StatsResponseDto>> findAll(
            @Valid StatsSearchDto searchDto
    ) {
        return ResponseUtils.createPaginatedResponse(statsService.findAll(
                RequestUtils.getSortAndPageRequest(searchDto),
                StatsSpecification.getSpecification(searchDto)
        ));
    }
}
