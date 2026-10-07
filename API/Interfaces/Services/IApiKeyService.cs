namespace API.Interfaces.Services;

public interface IApiKeyService
{
    bool ValidateApiKey(string apiKey);
}
