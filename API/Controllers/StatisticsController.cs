using API.Contracts.Responses;
using Microsoft.AspNetCore.Mvc;
using API.Interfaces.Services;

namespace API.Controllers;

[ApiController]
[Route("api/[controller]/[action]")]
public class StatisticsController(IStatisticsService statisticsService) : ControllerBase
{
    [HttpGet("{userId:guid}")]
    public async Task<ActionResult<GetStatisticsResponse>> GetStatistics(Guid userId, CancellationToken ct)
    {
        var statistics = await statisticsService.GetStatistics(userId, ct);
        return Ok(new GetStatisticsResponse(statistics));
    }
}
