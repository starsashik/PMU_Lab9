using API.Contracts.Requests;
using API.Contracts.Responses;
using API.Interfaces.Services;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers;

[ApiController]
[Route("api/[controller]/[action]")]
public class AuthorizationController(IAuthorizationService authorizationService) : ControllerBase
{
    [HttpPost]
    public async Task<ActionResult<RegisterUserResponse>> RegisterUser([FromBody] RegisterUserRequest request,
        CancellationToken ct)
    {
        var result = await authorizationService
            .RegisterUser(request.Name, request.Email, request.PasswordHash, ct);

        return Ok(new RegisterUserResponse(result));
    }

    [HttpPost]
    public async Task<ActionResult<LoginUserResponse>> LoginUser([FromBody] LoginUserRequest request,
        CancellationToken ct)
    {
        var result = await authorizationService
            .LoginUser(request.Email, request.PasswordHash, ct);

        return Ok(new LoginUserResponse(result));
    }
}