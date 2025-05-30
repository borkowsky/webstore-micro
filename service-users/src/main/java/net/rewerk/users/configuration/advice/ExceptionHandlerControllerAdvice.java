package net.rewerk.users.configuration.advice;

import jakarta.persistence.EntityNotFoundException;
import net.rewerk.exception.*;
import net.rewerk.webstore.dto.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            InvalidUploadTypeException.class,
            HttpMessageNotReadableException.class
    })
    public final ResponseEntity<ValidationExceptionResponse> handleException(Exception ex) {
        ValidationExceptionResponse response = new ValidationExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase()
        );
        if (ex instanceof MethodArgumentNotValidException ex1) {
            final List<FieldError> errors = ex1.getBindingResult().getFieldErrors();
            for (FieldError error : errors) {
                response.put(
                        error.getField(),
                        (error.getCodes() != null && Arrays.asList(error.getCodes()).contains("typeMismatch"))
                                ? "Invalid type"
                                : error.getDefaultMessage()
                );
            }
        } else if (ex instanceof HttpMessageNotReadableException) {
            response.put(
                    "error",
                    "Invalid input type"
            );
        } else {
            response.put(
                    "type",
                    "Invalid upload type"
            );
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            EntityNotFoundException.class,
            UserNotFoundException.class,
            CloudFileNotFound.class
    })
    public final ResponseEntity<EntityNotFoundExceptionResponse> handleNotFoundException(Exception ex) {
        EntityNotFoundExceptionResponse response = new EntityNotFoundExceptionResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({
            EmailExistsException.class,
            EntityExistsException.class,
            UsernameExistsException.class
    })
    public final ResponseEntity<EntityExistsExceptionResponse> handleConflictException(Exception ex) {
        EntityExistsExceptionResponse response = new EntityExistsExceptionResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            OperationInterruptedException.class
    })
    public final ResponseEntity<InternalServerErrorExceptionResponse> handleInternalErrorException(
            OperationInterruptedException ex
    ) {
        InternalServerErrorExceptionResponse response = new InternalServerErrorExceptionResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler({
            UnprocessableOperation.class
    })
    public final ResponseEntity<UnprocessableExceptionResponse> handleInternalErrorException(
            UnprocessableOperation ex
    ) {
        UnprocessableExceptionResponse response = new UnprocessableExceptionResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }
}
