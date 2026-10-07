namespace API.Contracts.Requests;

public record RegisterUserRequest(
    string Name,
    string Email,
    string PasswordHash
    );