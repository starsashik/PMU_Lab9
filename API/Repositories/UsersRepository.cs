using API.Interfaces.Repositories;
using Microsoft.EntityFrameworkCore;
using API.DataBase.Context;
using API.DataBase.Entities;
using API.Models;

namespace API.Repositories;

public class UsersRepository(MyDbContext context) : IUsersRepository
{
    public async Task<Guid> CreateUser(UserModel user, CancellationToken ct)
    {
        var userEntity = new User
        {
            Name =  user.Name,
            Email = user.Email,
            PasswordHash = user.PasswordHash
        };

        await context.Users.AddAsync(userEntity, ct);
        await context.SaveChangesAsync(ct);

        return userEntity.Id;
    }

    public async Task<List<UserModel>> GetAllUsers(CancellationToken ct)
    {
        var userEntities = await context.Users
            .AsNoTracking()
            .ToListAsync(ct);

        var users = userEntities
            .Select(userEntity => new UserModel
            {
                Id = userEntity.Id,
                Name =  userEntity.Name,
                Email = userEntity.Email,
                PasswordHash = userEntity.PasswordHash
            })
            .ToList();

        return users;
    }
}