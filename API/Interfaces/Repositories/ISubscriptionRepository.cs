using API.Models;

namespace API.Interfaces.Repositories;

public interface ISubscriptionRepository
{
    Task<Guid> CreateSubscription(SubscriptionModel subscription, CancellationToken ct);
    Task<List<SubscriptionModel>> GetAllSubscriptions(CancellationToken ct);
    Task<SubscriptionModel?> GetSubscriptionById(Guid subscriptionId, CancellationToken ct);
    Task<bool> UpdateSubscription(
        Guid subscriptionId,
        SubscriptionModel subscription,
        CancellationToken ct);
    Task<bool> DeleteSubscription(Guid subscriptionId, CancellationToken ct);
}
