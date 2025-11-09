package co.edu.uniquindio.Application.Exceptions;

import co.edu.uniquindio.Application.DTO.ResponseDTO;
import co.edu.uniquindio.Application.DTO.ValidationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> notFoundExceptionHandler(NotFoundException ex){
        return ResponseEntity.status(404).body( new ResponseDTO<>(true, HttpStatus.NOT_FOUND.value(), ex.getMessage()) );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseDTO<String>> validationExceptionHandler(ValidationException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( new ResponseDTO<>(true, HttpStatus.BAD_REQUEST.value(), ex.getMessage()) );
    }

    @ExceptionHandler(ValueConflictException.class)
    public ResponseEntity<ResponseDTO<String>> valueConflictExceptionHandler(ValueConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body( new ResponseDTO<>(true, HttpStatus.CONFLICT.value(), ex.getMessage()) );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDTO<String>> accessDeniedHandler(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseDTO<>(true, HttpStatus.FORBIDDEN.value(), "Access denied: " + ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDTO<String>> badCredentialsHandler(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseDTO<>(true, HttpStatus.UNAUTHORIZED.value(), "Invalid username or password"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<String>> exceptionHandler (Exception e){
        return ResponseEntity.internalServerError().body( new ResponseDTO<>(true, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()) );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<List<ValidationDTO>>> validationExceptionHandler ( MethodArgumentNotValidException ex ) {
        List<ValidationDTO> errors = new ArrayList<>();
        BindingResult results = ex.getBindingResult();
        for (FieldError e: results.getFieldErrors()) {
            errors.add( new ValidationDTO(e.getField(), e.getDefaultMessage()) );
        }
        return ResponseEntity.badRequest().body( new ResponseDTO<>(true, HttpStatus.BAD_REQUEST.value(), errors) );
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ResponseDTO<String>> invalidOperationHandler(InvalidOperationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO<>(true, HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> resourceNotFoundHandler(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDTO<>(true, HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }
}