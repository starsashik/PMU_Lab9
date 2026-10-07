using API.Contracts.Requests;
using API.Contracts.Responses;
using API.Interfaces.Services;
using API.Models;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers;

[ApiController]
[Route("api/[controller]/[action]")]
public class UsersController(IUsersService usersService) : ControllerBase
{
    [HttpGet]
    public async Task<ActionResult<GetAllUsersResponse>> GetUsers(CancellationToken ct)
    {
        var users = await usersService.GetAllUsers(ct);
        return Ok(new GetAllUsersResponse(users));
    }

    [HttpGet("{id:guid}")]
    public async Task<ActionResult<GetUserResponse>> GetUser(Guid id, CancellationToken ct)
    {
        var user = await usersService.GetUserById(id, ct);
        return user is null ? NotFound() : Ok(new GetUserResponse(user));
    }

    [HttpPost]
    public async Task<ActionResult<CreateUserResponse>> CreateUser(
        [FromBody] CreateUserRequest request,
        CancellationToken ct)
    {
        var user = new UserModel
        {
            Name = request.Name,
            Email = request.Email,
            PasswordHash = request.PasswordHash
        };
        var createdUserId = await usersService.CreateUser(user, ct);
        return createdUserId is null ? BadRequest() : Ok(new CreateUserResponse(createdUserId.Value));
    }
}