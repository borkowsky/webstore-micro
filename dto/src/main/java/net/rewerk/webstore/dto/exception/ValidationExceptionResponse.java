package net.rewerk.webstore.dto.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validation exception response DTO
 *
 * @author rewerk
 */

@Getter
public class ValidationExceptionResponse {
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
    private final String timestamp;
    /**
     * List of errors
     */
    private final Map<String, List<String>> errors;

    public ValidationExceptionResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = String.valueOf(Instant.now());
        this.errors = new HashMap<>();
    }

    public void put(String field, String message) {
        errors.computeIfAbsent(field, _ -> new ArrayList<>()).add(message);
    }
}
