using API.Interfaces;
using API.Interfaces.Repositories;
using API.Interfaces.Services;
using API.Models;

namespace API.Services;

public class UsersService(IUsersRepository usersRepository) : IUsersService
{
    public async Task<Guid?> CreateUser(UserModel user, CancellationToken ct)
    {
        try
        {
            var createdUserId = await usersRepository.CreateUser(user, ct);
            return createdUserId;
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            return null;
        }
    }

    public async Task<List<UserModel>> GetAllUsers(CancellationToken ct)
    {
        try
        {
            var users = await usersRepository.GetAllUsers(ct);
            return users;
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            return [];
        }
    }

    public async Task<UserModel?> GetUserById(Guid userId, CancellationToken ct)
    {
        try
        {
            var allUsers = await usersRepository.GetAllUsers(ct);
            var user = allUsers.FirstOrDefault(u => u.Id == userId);
            if (user == null)
                throw new Exception("Unknown user id");
            return user;
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            return null;
        }
    }
}
