package com.example.policee.util.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {


        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<String> handle(RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }


        // 2. @Valid ilə bağlı olan Validation xətalarını tutur (Məs: Çəki 30-dan azdır)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }


        @ExceptionHandler(OtpCodeIsNotCorrectException.class)
        public ResponseEntity<Object> handleResourceNotFound(OtpCodeIsNotCorrectException ex) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }


    @ExceptionHandler(UserBlockaException.class)
    public ResponseEntity<Object> handleUserBlock(UserBlockaException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(userAlreadyExist.class)
    public ResponseEntity<Object> handleUserAlreadyExsix(userAlreadyExist ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }



    @ExceptionHandler(Exception.class)
        public ResponseEntity<Object> handleGeneralException(Exception ex) {
            Map<String, Object> body = new HashMap<>();
            // Bura ex.getMessage() əlavə edirik ki, xətanı görək
            body.put("message", ex.getMessage());
            ex.printStackTrace(); // Terminala tam xətanı çap edir
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }


}
