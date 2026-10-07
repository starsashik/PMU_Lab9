using API.Interfaces.Services;
using API.Models;

namespace API.Services;

public class StatisticsService(ISubscriptionsService subscriptionsService) : IStatisticsService
{

    public async Task<StatisticsModel> GetStatistics(Guid userId, CancellationToken ct = default)
    {
        var subscriptions = (await subscriptionsService.GetAllSubscriptions(ct))
            .Where(subscription => subscription.UserId == userId)
            .ToList();

        var activeSubscriptions = subscriptions
            .Where(subscription => subscription.IsActive == true)
            .ToList();

        var today = DateOnly.FromDateTime(DateTime.UtcNow);

        var upcomingPayments = activeSubscriptions
            .Where(subscription =>
                subscription.NextPaymentDate >= today &&
                subscription.NextPaymentDate <= today.AddDays(7))
            .ToList();

        var nearestPayment = activeSubscriptions
            .Where(subscription => subscription.NextPaymentDate >= today)
            .OrderBy(subscription => subscription.NextPaymentDate)
            .FirstOrDefault();

        var mostExpensiveSubscription = activeSubscriptions
            .OrderByDescending(GetMonthlyCost)
            .FirstOrDefault();

        return new StatisticsModel
        {
            UserId = userId,
            TotalSubscriptions = subscriptions.Count,
            ActiveSubscriptions = activeSubscriptions.Count,
            InactiveSubscriptions = subscriptions.Count - activeSubscriptions.Count,
            MonthlyExpenses = Math.Round(activeSubscriptions.Sum(GetMonthlyCost), 2),
            YearlyExpenses = Math.Round(activeSubscriptions.Sum(GetYearlyCost), 2),
            PaymentsNextSevenDays = upcomingPayments.Count,
            PaymentsAmountNextSevenDays = Math.Round(
                upcomingPayments.Sum(subscription => subscription.Price),
                2),
            NearestPaymentDate = nearestPayment?.NextPaymentDate,
            MostExpensiveSubscriptionName = mostExpensiveSubscription?.Name,
            MostExpensiveSubscriptionMonthlyCost = mostExpensiveSubscription is null
                ? null
                : Math.Round(GetMonthlyCost(mostExpensiveSubscription), 2),
            LastUpdated = DateTime.UtcNow
        };
    }

    private static decimal GetMonthlyCost(SubscriptionModel subscription)
    {
        return subscription.PaymentPeriod.Trim().ToLowerInvariant() switch
        {
            "weekly" => subscription.Price * 52 / 12,
            "monthly" => subscription.Price,
            "quarterly" => subscription.Price / 3,
            "yearly" => subscription.Price / 12,
            _ => 0
        };
    }

    private static decimal GetYearlyCost(SubscriptionModel subscription)
    {
        return subscription.PaymentPeriod.Trim().ToLowerInvariant() switch
        {
            "weekly" => subscription.Price * 52,
            "monthly" => subscription.Price * 12,
            "quarterly" => subscription.Price * 4,
            "yearly" => subscription.Price,
            _ => 0
        };
    }
}
