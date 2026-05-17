package com.elktini.iotfleet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Specifically listening for validation errors (@Valid)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // HTTP code 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // We extract each field with an error and retrieve our personalized message.
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    //
    @ResponseStatus(HttpStatus.NOT_FOUND) // HTTP code 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public Map <String, String> handleNotFoundExceptions(ResourceNotFoundException ex) {

        Map<String, String> error = new HashMap<>();

        // Retrieve the message passed to the exception when it was created.
        error.put("error", ex.getMessage());

        return error;
    }
}
