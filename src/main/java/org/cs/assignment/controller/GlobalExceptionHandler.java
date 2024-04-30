package org.cs.assignment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.hibernate.PropertyValueException;
import org.hibernate.PersistentObjectException;
import org.hibernate.HibernateException;

import java.util.Collections;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Collections.singletonMap("details", ex.getReason()));
    }

    @ExceptionHandler(PropertyValueException.class)
    public ResponseEntity<Map<String, Object>> handlePropertyValueException(PropertyValueException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("details", ex.getMessage()));
    }

    @ExceptionHandler(PersistentObjectException.class)
    public ResponseEntity<Map<String, Object>> handlePersistentObjectException(PersistentObjectException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("details", ex.getMessage() + ". Hint: do not include id in request body"));
    }

    @ExceptionHandler(HibernateException.class)
    public ResponseEntity<Map<String, Object>> handleHibernateException(HibernateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("details", ex.getMessage() + ". Hint: do not include id in request body"));
    }
}
