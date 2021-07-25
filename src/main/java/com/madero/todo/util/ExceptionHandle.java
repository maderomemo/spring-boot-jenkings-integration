package com.madero.todo.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.madero.todo.exception.TaskException;

@ControllerAdvice
public class ExceptionHandle extends ResponseEntityExceptionHandler{
	
	private static final String ERROR_MESSAGE_KEY = "error";
	private static final String STATUS_MESSAGE_KEY = "status";
	private static final String TIME_MESSAGE_KEY = "timestamp";
	
	
	@ExceptionHandler({ TaskException.class })
    public ResponseEntity<Object> handleTaskException(Exception ex, WebRequest request) {
        return buildResponseObject(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
	                 HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
	    return buildResponseObject(errors , HttpStatus.BAD_REQUEST);
	}
		
	@ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
        return buildResponseObject(ex.getMessage() , HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	private ResponseEntity<Object> buildResponseObject(Object errors, HttpStatus httpStatus){
	    Map<String, Object> body = new HashMap<>();
        body.put(TIME_MESSAGE_KEY, new Date());
        body.put(STATUS_MESSAGE_KEY, httpStatus);
        body.put(ERROR_MESSAGE_KEY, errors);
        return new ResponseEntity<Object>(body , new HttpHeaders(), httpStatus);
	}

}
