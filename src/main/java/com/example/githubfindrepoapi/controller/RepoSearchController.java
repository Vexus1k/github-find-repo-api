package com.example.githubfindrepoapi.controller;

import com.example.githubfindrepoapi.exception.UserNotFoundException;
import com.example.githubfindrepoapi.logic.ErrorResponseGenerator;
import com.example.githubfindrepoapi.logic.RepoSearchService;
import com.example.githubfindrepoapi.model.ErrorMessage;
import com.example.githubfindrepoapi.model.dto.UserRepositoriesDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class RepoSearchController {
    private final RepoSearchService repoSearchService;
    private final ErrorResponseGenerator errorResponseGenerator;

    public RepoSearchController(RepoSearchService repoSearchService, ErrorResponseGenerator errorResponseGenerator) {
        this.repoSearchService = repoSearchService;
        this.errorResponseGenerator = errorResponseGenerator;
    }

    @GetMapping(value = "/repositories")
    public ResponseEntity<?> searchRepositories(
            @RequestParam String username,
            @RequestHeader(name = "Accept") String acceptHeader
    ) throws JsonProcessingException {
        if (!acceptHeader.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
            return errorResponseGenerator.createJsonErrorResponse(HttpStatus.NOT_ACCEPTABLE, "Unsupported Accept header");
        }

        try {
            UserRepositoriesDTO userRepositories = repoSearchService.getUserRepositories(username);
            return ResponseEntity.ok(userRepositories.getRepositories());
        } catch (UserNotFoundException e) {
            return errorResponseGenerator.createJsonErrorResponse(HttpStatus.NOT_FOUND, "User not found");
        } catch (HttpClientErrorException e) {
            int statusCode = e.getStatusCode().value();
            return ResponseEntity.status(statusCode)
                    .body(new ErrorMessage(statusCode, e.getMessage()));
        }
    }
}



