# GitHub Repository Search API

This project is a Spring Boot application that interacts with the GitHub API to provide information about a user's GitHub repositories that are not forks. It supports JSON responses and handles error cases properly.

## Table of Contents

- [Features](#features)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Error Responses](#error-responses)

## Features

- List user's GitHub repositories that are not forks.
- Provide repository name, owner login, branch names, and last commit sha.

## API Endpoints

- `GET /repositories`
    - Request parameters:
        - `username` (required): GitHub username
    - Request headers:
        - `Accept: application/json` (required)
    - Response:
        - Status: 200 OK
        - JSON body: List of repositories with details
    - Example request:
      ```
      GET /repositories?username=exampleUser
      Accept: application/json
      ```

## Getting Started

1. Clone this repository.
2. Configure your GitHub API token in `application.yml`.
3. Build and run the Spring Boot application.

## Usage

1. Make a GET request to `/repositories` with the required parameters and headers.
2. Receive a JSON response with the list of repositories and their details.

## Error Responses

### User Not Found

- **Status**: 404 Not Found
- **JSON body**:
  ~~~
  {
      "status": 404,
      "message": "User not found"
  }
  ~~~

### Unsupported Accept Header

- **Status**: 406 Not Acceptable
- **JSON body**:
  ~~~
  {
      "status": 406,
     "message": "Unsupported Accept header"
  }
  ~~~
