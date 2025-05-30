package net.rewerk.webstore.reviews.controller;

import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.entity.Review;
import net.rewerk.webstore.reviews.service.entity.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Parametrized REST controller for Review service
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reviews/{id:\\d+}")
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * Method for populate review method attribute by Review identifier request mapping path variable
     *
     * @param id Review identifier
     * @return Review entity
     */

    @ModelAttribute("review")
    public Review getReview(@PathVariable Integer id) {
        return reviewService.findById(id);
    }

    /**
     * DELETE endpoint for delete Review entity
     *
     * @param review Model attribute - review
     * @return Response with status 204 No Content without payload
     */

    @DeleteMapping
    public ResponseEntity<Void> deleteReview(@ModelAttribute("review") Review review) {
        reviewService.delete(review);
        return ResponseEntity.noContent().build();
    }
}
