namespace API.Contracts.Requests;

public record CreateUserRequest(
    string Name,
    string Email,
    string PasswordHash
);
