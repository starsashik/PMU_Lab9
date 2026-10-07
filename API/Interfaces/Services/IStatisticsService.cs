using API.Models;

namespace API.Interfaces.Services;

public interface IStatisticsService
{
    Task<StatisticsModel> GetStatistics(Guid userId, CancellationToken ct);
}
