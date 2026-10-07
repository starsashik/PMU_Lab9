namespace API.Contracts.Requests;

public record LoginUserRequest(
    string Email,
    string PasswordHash
    );