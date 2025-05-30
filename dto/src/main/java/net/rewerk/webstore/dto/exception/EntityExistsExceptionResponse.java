package net.rewerk.webstore.dto.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Entity Exists exception response DTO
 *
 * @author rewerk
 */

@Getter
public class EntityExistsExceptionResponse {
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

    public EntityExistsExceptionResponse(String details) {
        this.code = HttpStatus.CONFLICT.value();
        this.message = HttpStatus.CONFLICT.getReasonPhrase();
        this.details = details;
        this.timestamp = String.valueOf(Instant.now());
    }
}
