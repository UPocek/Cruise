package com.cruise.Cruise.tools;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@RestController
public class ResponseErrorHandler {
    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<?> handleAccessDeniedException(Exception ex, WebRequest request) {
        ex.printStackTrace();
        return new ResponseEntity<>("Access denied!", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<?> handleParamException(MethodArgumentNotValidException ex) {
        StringBuilder paramErrorList = new StringBuilder();
        for (ObjectError e : ex.getBindingResult().getAllErrors()) {
            String field = ((FieldError) e).getField();
            String message = e.getDefaultMessage();
            paramErrorList.append("Field ").append(field).append(" ").append(message).append("\n");
        }
        System.err.println(paramErrorList.toString());
        return new ResponseEntity<>(paramErrorList.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<?> handleParamWrongFormatException(MethodArgumentTypeMismatchException ex) {
        String paramErrorList = "Field " + ex.getName() + " " + "format is not valid!";
        return new ResponseEntity<>(paramErrorList, HttpStatus.BAD_REQUEST);
    }
}