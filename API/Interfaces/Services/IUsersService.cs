using API.Models;

namespace API.Interfaces.Services;

public interface IUsersService
{
    Task<Guid?> CreateUser(UserModel user, CancellationToken ct);
    Task<List<UserModel>> GetAllUsers(CancellationToken ct);
    Task<UserModel?> GetUserById(Guid id, CancellationToken ct);
}
