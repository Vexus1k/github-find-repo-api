package com.example.githubfindrepoapi.logic;

import com.example.githubfindrepoapi.model.ErrorMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ErrorResponseGenerator {
    private final ObjectMapper objectMapper;

    public ErrorResponseGenerator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<String> createJsonErrorResponse(HttpStatus status, String message) throws JsonProcessingException {
        ErrorMessage errorMessage = new ErrorMessage(status.value(), message);
        String jsonErrorMessage = objectMapper.writeValueAsString(errorMessage);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonErrorMessage);
    }
}

