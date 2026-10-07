namespace API.Interfaces.Services;

public interface IAuthorizationService
{
    Task<Guid?> RegisterUser(string name, string email, string password, CancellationToken ct);
    Task<Guid?> LoginUser(string email, string password, CancellationToken ct);
}