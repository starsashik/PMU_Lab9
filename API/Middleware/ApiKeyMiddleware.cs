using API.Interfaces;
using API.Interfaces.Services;

namespace API.Middleware;

public class ApiKeyMiddleware
{
    private readonly RequestDelegate _next;
    private readonly IApiKeyService _apiKeyService;

    public ApiKeyMiddleware(RequestDelegate next, IApiKeyService apiKeyService)
    {
        _next = next;
        _apiKeyService = apiKeyService;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        if (!context.Request.Path.StartsWithSegments("/api"))
        {
            await _next(context);
            return;
        }

        if (!context.Request.Headers.TryGetValue("X-API-Key", out var extractedApiKey))
        {
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            await context.Response.WriteAsync("API-ключ не указан");
            return;
        }

        if (!_apiKeyService.ValidateApiKey(extractedApiKey!))
        {
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            await context.Response.WriteAsync("Неверный API-ключ");
            return;
        }

        await _next(context);
    }
}
