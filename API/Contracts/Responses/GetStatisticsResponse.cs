using API.Models;

namespace API.Contracts.Responses;

public record GetStatisticsResponse(
    StatisticsModel Statistics
    );
