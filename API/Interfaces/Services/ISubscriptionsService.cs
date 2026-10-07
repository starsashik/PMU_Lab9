using API.Models;

namespace API.Interfaces.Services;

public interface ISubscriptionsService
{
    Task<Guid?> CreateSubscription(SubscriptionModel subscription, CancellationToken ct);
    Task<List<SubscriptionModel>> GetAllSubscriptions(CancellationToken ct);
    Task<SubscriptionModel?> GetSubscriptionById(Guid id, CancellationToken ct);
    Task<bool> UpdateSubscription(Guid id, SubscriptionModel subscription, CancellationToken ct );
    Task<bool> DeleteSubscription(Guid id, CancellationToken ct);
}
