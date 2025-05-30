package net.rewerk.webstore.dto.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Entity Not Found exception response DTO
 *
 * @author rewerk
 */


@Getter
public class EntityNotFoundExceptionResponse {
    /**
     * Code of exception
     */
    private final int code;
    /**
     * Reason phrase of exception
     */
    private final String message;
    /**
     * Exception details
     */
    private final String details;
    /**
     * Timestamp of exception occurrence
     */
    private final String timestamp;

    public EntityNotFoundExceptionResponse(String details) {
        this.code = HttpStatus.NOT_FOUND.value();
        this.message = HttpStatus.NOT_FOUND.getReasonPhrase();
        this.details = details;
        this.timestamp = String.valueOf(Instant.now());
    }
}
