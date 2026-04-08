package com.justiceserve.identityservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,Object>> notFound(ResourceNotFoundException e) { return ResponseEntity.status(404).body(err(404,e.getMessage())); }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String,Object>> bad(BadRequestException e) { return ResponseEntity.status(400).body(err(400,e.getMessage())); }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> denied(AccessDeniedException e) { return ResponseEntity.status(403).body(err(403,"Access denied")); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> val(MethodArgumentNotValidException e) {
        Map<String,String> m=new LinkedHashMap<>();
        e.getBindingResult().getAllErrors().forEach(err->m.put(((FieldError)err).getField(),err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(m);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> gen(Exception e) { return ResponseEntity.status(500).body(err(500,"Error: "+e.getMessage())); }
    private Map<String,Object> err(int s,String m) {
        Map<String,Object> b=new LinkedHashMap<>(); b.put("status",s); b.put("message",m); b.put("timestamp",LocalDateTime.now().toString()); return b;
    }
}
