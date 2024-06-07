package br.com.ecommerce.orders.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private final String CREDENTIALS_ERROR_MESSAGE = "Bad credentials";
	private final String METHOD_ARGUMENT_NOT_VALID_MESSAGE = "Input validation error";
	private final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";

	
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handlerException(Exception ex) {
		ex.printStackTrace();
        return ResponseEntity
        		.internalServerError()
        		.body(new ErrorMessage(
        				HttpStatus.INTERNAL_SERVER_ERROR.value(),
        				INTERNAL_SERVER_ERROR_MESSAGE));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageWithFields> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    	var fields = ex.getFieldErrors().stream()
    			.map(f -> new FieldErrorResponse(f.getField().toString(), f.getDefaultMessage()));
    	var response = new ErrorMessageWithFields(
    			HttpStatus.BAD_REQUEST.value(),
    			METHOD_ARGUMENT_NOT_VALID_MESSAGE,
    			fields);
    	
    	return ResponseEntity
    			.badRequest()
    			.body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorMessage> handlerEntityNotFoundException(EntityNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorMessage(
						HttpStatus.UNAUTHORIZED.value(), 
						CREDENTIALS_ERROR_MESSAGE));
	}
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handlerHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity
        		.badRequest()
        		.body(ex.getMessage());
    }

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorMessage> handlerIllegalArgumentException(IllegalArgumentException ex) {
		return ResponseEntity
				.badRequest()
				.body(new ErrorMessage(
						HttpStatus.BAD_REQUEST.value(), 
						ex.getMessage()));
	}
	
	@ExceptionHandler(OutOfStockException.class)
	public ResponseEntity<ErrorMessageWithProducts> handlerOutOfStockException(OutOfStockException ex) {
		return ResponseEntity
				.badRequest()
				.body(new ErrorMessageWithProducts(
						HttpStatus.BAD_REQUEST.value(), 
						ex.getMessage(),
						ex.getProducts()));
	}
	
	private record ErrorMessage(int status, String error) {}
	private record ErrorMessageWithFields(int status, String error, Object fields) {}
	private record ErrorMessageWithProducts(int status, String error, List<ProductOutOfStockDTO> products) {}
	private record FieldErrorResponse(String field, String message) {}
}