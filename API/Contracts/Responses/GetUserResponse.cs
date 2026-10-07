using API.Models;

namespace API.Contracts.Responses;

public record GetUserResponse(
    UserModel User
    );
