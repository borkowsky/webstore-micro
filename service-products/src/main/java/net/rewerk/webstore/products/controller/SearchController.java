package net.rewerk.webstore.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.search.SearchRequestDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.search.SearchResponseDto;
import net.rewerk.webstore.products.service.SearchService;
import net.rewerk.webstore.utility.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for provide search
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/search")
public class SearchController {
    private final SearchService searchService;

    /**
     * GET endpoint to provide search
     *
     * @param requestDto DTO with search parameters
     * @return Response with status 200 OK and single payload of SearchResponseDto
     */

    @GetMapping
    public ResponseEntity<SinglePayloadResponseDto<SearchResponseDto>> doSearch(
            @Valid SearchRequestDto requestDto
    ) {
        return ResponseUtils.createSingleResponse(searchService.search(requestDto));
    }
}
