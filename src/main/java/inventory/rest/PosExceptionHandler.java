package inventory.rest;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import inventory.entity.Message;


@ControllerAdvice
public class PosExceptionHandler {
	
	@ExceptionHandler
	public ResponseEntity<Message> handleException(SQLIntegrityConstraintViolationException exc) {
		
		
		Message error = new Message(false, exc.getMessage());	
			
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	public ResponseEntity<Message> handleException(Exception exc) {
		
		
		Message error = new Message(false, exc.getMessage());	
			
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
}
