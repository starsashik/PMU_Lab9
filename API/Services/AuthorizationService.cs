using API.Interfaces.Services;
using API.Models;

namespace API.Services;

public class AuthorizationService(IUsersService usersService) : IAuthorizationService
{
    public async Task<Guid?> RegisterUser(string name, string email, string password, CancellationToken ct)
    {
        var existedUser = (await usersService.GetAllUsers(ct))
            .FirstOrDefault(u => u.Email == email);

        if (existedUser is not null) return null;

        var createdUserId = await usersService
            .CreateUser(new UserModel { Id = Guid.NewGuid(), Name = name, Email = email, PasswordHash = password }, ct);

        return createdUserId;
    }

    public async Task<Guid?> LoginUser(string email, string password, CancellationToken ct)
    {
        var existedUser = (await usersService.GetAllUsers(ct))
            .FirstOrDefault(u => u.Email == email);

        if (existedUser is null) return null;

        if (existedUser.PasswordHash != password) return null;

        return existedUser.Id;
    }
}
