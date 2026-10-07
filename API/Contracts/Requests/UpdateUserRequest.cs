namespace API.Contracts.Requests;

public record UpdateUserRequest(
    string Name,
    string Email,
    string PasswordHash
);
