package com.softserve.patientsservice.utils.config;

import com.softserve.patientsservice.utils.exceptions.CustomEntityNotFoundException;
import com.softserve.patientsservice.utils.exceptions.CustomFailedToDeleteEntityException;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Log
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {CustomFailedToDeleteEntityException.class})
    protected ResponseEntity<Object> handleFailedToDeleteEntity(RuntimeException ex, WebRequest request){
        log.warning(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(),
        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {CustomEntityNotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFound(RuntimeException ex, WebRequest request){
        log.warning(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
