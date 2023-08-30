package com.example.githubfindrepoapi.model.dto;

import java.util.List;

public class UserRepositoriesDTO {
    private List<GitHubRepositoryDTO> repositories;

    public List<GitHubRepositoryDTO> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<GitHubRepositoryDTO> repositories) {
        this.repositories = repositories;
    }
}
