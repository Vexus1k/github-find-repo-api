package com.example.githubfindrepoapi.logic;

import com.example.githubfindrepoapi.exception.UserNotFoundException;
import com.example.githubfindrepoapi.model.Branch;
import com.example.githubfindrepoapi.model.GitHubRepository;
import com.example.githubfindrepoapi.model.dto.BranchDTO;
import com.example.githubfindrepoapi.model.dto.GitHubRepositoryDTO;
import com.example.githubfindrepoapi.model.dto.UserRepositoriesDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepoSearchService {
    @Value("${github.token}")
    private String githubToken;

    private final RestTemplate restTemplate;
    private final String githubApiUrl;

    public RepoSearchService(RestTemplate restTemplate, @Value("${github.api.url}") String githubApiUrl) {
        this.restTemplate = restTemplate;
        this.githubApiUrl = githubApiUrl;
    }

    public UserRepositoriesDTO getUserRepositories(String username) {
        String userApiUrl = githubApiUrl + "/users/" + username + "/repos";

        ResponseEntity<GitHubRepository[]> response;
        try {
            response = restTemplate.exchange(
                    userApiUrl,
                    HttpMethod.GET,
                    createAuthorizedRequestEntity(),
                    GitHubRepository[].class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new UserNotFoundException("User not found");
            }

            throw new RuntimeException("Failed to fetch user repositories from API", e);
        }

        GitHubRepository[] repositories = response.getBody();

        if (repositories != null && repositories.length > 0) {
            List<GitHubRepositoryDTO> nonForkRepos = filterNonForkRepositories(repositories);

            UserRepositoriesDTO userRepositoriesDTO = new UserRepositoriesDTO();
            userRepositoriesDTO.setRepositories(nonForkRepos);

            return userRepositoriesDTO;
        }

        return null;
    }

    private List<GitHubRepositoryDTO> filterNonForkRepositories(GitHubRepository[] repositories) {
        return Arrays.stream(repositories)
                .filter(repo -> !repo.isFork())
                .map(this::mapToRepositoryInfoDTO)
                .collect(Collectors.toList());
    }

    private List<BranchDTO> fetchBranches(String repositoryUrl) {
        String url = repositoryUrl.replace("{/branch}", "");

        ResponseEntity<Branch[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                createAuthorizedRequestEntity(),
                Branch[].class
        );

        Branch[] branches = response.getBody();

        if (branches != null) {
            return Arrays.stream(branches)
                    .map(branch -> new BranchDTO(branch.getName(), branch.getCommit().getSha()))
                    .collect(Collectors.toList());
        }

        throw new RuntimeException("Failed to fetch branches from API");
    }

    private GitHubRepositoryDTO mapToRepositoryInfoDTO(GitHubRepository repo) {
        GitHubRepositoryDTO dto = new GitHubRepositoryDTO();
        dto.setRepositoryName(repo.getName());
        dto.setOwnerLogin(repo.getOwner().getLogin());

        List<BranchDTO> branchesInfo = fetchBranches(repo.getBranches_url());

        dto.setBranches(branchesInfo);
        return dto;
    }

    private HttpEntity<String> createAuthorizedRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.githubToken);
        return new HttpEntity<>(headers);
    }
}


