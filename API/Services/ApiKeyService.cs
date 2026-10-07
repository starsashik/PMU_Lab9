using API.Interfaces;
using API.Interfaces.Services;

namespace API.Services;

public class ApiKeyService : IApiKeyService
{
    private readonly HashSet<string> _validApiKeys =
    [
        "helpdesk-api-key"
    ];

    public bool ValidateApiKey(string apiKey)
    {
        return _validApiKeys.Contains(apiKey);
    }
}
