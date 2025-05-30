package net.rewerk.webstore.utility;

import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Common servlet response utility methods
 *
 * @author rewerk
 */

public abstract class ResponseUtils {

    /**
     * Form response entity with payload with page of entities
     *
     * @param page Page of entities
     * @param <T>  Generic type of entity
     * @return Response entity with status 200 OK and payload with page of entities
     */

    public static <T> ResponseEntity<PaginatedPayloadResponseDto<T>> createPaginatedResponse(
            Page<T> page
    ) {
        return ResponseEntity.ok(PaginatedPayloadResponseDto.<T>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(page.getContent())
                .total(page.getTotalElements())
                .page(page.getNumber() + 1)
                .pages(page.getTotalPages())
                .build());
    }

    /**
     * Form response entity with payload with collection of entities
     *
     * @param items List of entities
     * @param <T>   Generic type of entity
     * @return Response entity with status 200 OK and payload with collection of entities
     */

    public static <T> ResponseEntity<PayloadResponseDto<T>> createCollectionResponse(
            List<T> items
    ) {
        return ResponseEntity.ok(PayloadResponseDto.<T>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(items)
                .build());
    }

    /**
     * Form response entity with payload with single entity
     *
     * @param item Entity
     * @param <T>  Generic type of entity
     * @return Response entity with status 200 OK and payload with single entity
     */

    public static <T> ResponseEntity<SinglePayloadResponseDto<T>> createSingleResponse(
            T item
    ) {
        return ResponseEntity.ok(SinglePayloadResponseDto.<T>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(item)
                .build());
    }
}
