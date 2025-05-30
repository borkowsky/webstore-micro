package net.rewerk.webstore.dto.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Unprocessable exception response DTO
 *
 * @author rewerk
 */

@Getter
public class UnprocessableExceptionResponse {
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

    public UnprocessableExceptionResponse(String details) {
        this.code = HttpStatus.UNPROCESSABLE_ENTITY.value();
        this.message = HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase();
        this.details = details;
        this.timestamp = String.valueOf(Instant.now());
    }
}
