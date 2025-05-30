package net.rewerk.webstore.products.feign.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * Feign client error decoder class
 *
 * @author rewerk
 */

@Slf4j
public class FeignClientErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder delegate = new Default();

    /**
     * Feign error decoder
     *
     * @param methodKey Signature of feign method
     * @param response  Raw feign response
     * @return Decoded exception
     */

    @Override
    public Exception decode(String methodKey, Response response) {
        log.info("FeignClientErrorDecoder.decode() called. methodKey = {}, response status = {}",
                methodKey,
                response.status());
        ExceptionMessage message;
        if (response.body() == null) {
            return this.delegate.decode(methodKey, response);
        }
        try (InputStream bodyIs = response.body()
                .asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ExceptionMessage.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        return switch (response.status()) {
            case 400 -> new BadRequestException(message.getMessage() != null ? message.getMessage() : "Bad Request");
            case 404 -> new NotFoundException(message.getMessage() != null ? message.getMessage() : "Not found");
            default -> this.delegate.decode(methodKey, response);
        };
    }

    /**
     * Static Exception message class DTO
     */

    @Data
    private static class ExceptionMessage {
        private String timestamp;
        private int status;
        private int code;
        private String error;
        private String message;
        private String details;
        private String path;
        private String requestId;
    }
}
