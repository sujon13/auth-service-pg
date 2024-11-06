package example.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class exceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();

            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                errors.put(fieldName, errorMessage);
            } else {
                String objectName = error.getObjectName();
                errors.put(objectName, errorMessage);
            }
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
        // Customize the response as needed
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Authentication failed: " + ex.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<String> handleForbiddenException(HttpClientErrorException.Forbidden ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }
}
