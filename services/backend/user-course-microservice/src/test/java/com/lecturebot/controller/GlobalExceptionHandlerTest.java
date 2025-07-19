package com.lecturebot.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleAccessDeniedException_returnsForbidden() {
        AccessDeniedException ex = new AccessDeniedException("nope");
        ResponseEntity<Object> response = handler.handleAccessDeniedException(ex);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(((GlobalExceptionHandler.ErrorResponse) response.getBody()).getError().contains("Access denied: nope"));
    }

    @Test
    void handleValidationException_returnsBadRequest() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.toString()).thenReturn("binding error");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<Object> response = handler.handleValidationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(((GlobalExceptionHandler.ErrorResponse) response.getBody()).getError().contains("Validation error: binding error"));
    }

    @Test
    void handleAllExceptions_returnsInternalServerError() {
        Exception ex = new Exception("fail");
        ResponseEntity<Object> response = handler.handleAllExceptions(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(((GlobalExceptionHandler.ErrorResponse) response.getBody()).getError().contains("Internal server error: fail"));
    }
}
