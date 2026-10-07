using API.Models;

namespace API.Interfaces.Repositories;

public interface IUsersRepository
{
    Task<Guid> CreateUser(UserModel user, CancellationToken ct);
    Task<List<UserModel>> GetAllUsers(CancellationToken ct);
}