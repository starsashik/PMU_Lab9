using API.Models;

namespace API.Contracts.Responses;

public record GetAllUsersResponse(
    List<UserModel> Users
    );
