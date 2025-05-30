package net.rewerk.webstore.dto.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Internal Server Error exception response DTO
 *
 * @author rewerk
 */

@Getter
public class InternalServerErrorExceptionResponse {
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

    public InternalServerErrorExceptionResponse(String details) {
        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.message = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        this.details = details;
        this.timestamp = String.valueOf(Instant.now());
    }
}
