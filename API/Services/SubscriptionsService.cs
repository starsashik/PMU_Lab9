using API.Interfaces.Repositories;
using API.Interfaces.Services;
using API.Models;

namespace API.Services;

public class SubscriptionsService(ISubscriptionRepository subscriptionRepository) : ISubscriptionsService
{
    public async Task<Guid?> CreateSubscription(SubscriptionModel subscription, CancellationToken ct)
    {
        try
        {
            var createdSubscriptionId = await subscriptionRepository.CreateSubscription(subscription, ct);
            return createdSubscriptionId;
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            return null;
        }
    }

    public async Task<List<SubscriptionModel>> GetAllSubscriptions(CancellationToken ct = default)
    {
        try
        {
            return await subscriptionRepository.GetAllSubscriptions(ct);
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            return [];
        }
    }

    public async Task<SubscriptionModel?> GetSubscriptionById(Guid id, CancellationToken ct = default)
    {
        try
        {
            return await subscriptionRepository.GetSubscriptionById(id, ct);
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            return null;
        }
    }

    public async Task<bool> UpdateSubscription(Guid id, SubscriptionModel subscription, CancellationToken ct = default)
    {
        try
        {
            return await subscriptionRepository.UpdateSubscription(id, subscription, ct);
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            return false;
        }
    }

    public async Task<bool> DeleteSubscription(Guid id, CancellationToken ct = default)
    {
        try
        {
            return await subscriptionRepository.DeleteSubscription(id, ct);
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            return false;
        }
    }
}
