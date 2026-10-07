using API.Interfaces.Repositories;
using Microsoft.EntityFrameworkCore;
using API.DataBase.Context;
using API.DataBase.Entities;
using API.Models;

namespace API.Repositories;

public class SubscriptionRepository(MyDbContext context) : ISubscriptionRepository
{
    public async Task<Guid> CreateSubscription(SubscriptionModel subscription, CancellationToken ct)
    {
        var subscriptionEntity = new Subscription
        {
            Name = subscription.Name,
            Description = subscription.Description,
            Price = subscription.Price,
            PaymentPeriod = subscription.PaymentPeriod,
            NextPaymentDate = subscription.NextPaymentDate,
            NotificationDaysBefore = subscription.NotificationDaysBefore,
            IsActive = subscription.IsActive,
            UserId = subscription.UserId
        };

        await context.Subscriptions.AddAsync(subscriptionEntity, ct);
        await context.SaveChangesAsync(ct);

        return subscriptionEntity.Id;
    }

    public Task<List<SubscriptionModel>> GetAllSubscriptions(CancellationToken ct)
    {
        return context.Subscriptions
            .AsNoTracking()
            .Select(subscription => new SubscriptionModel
            {
                Id = subscription.Id,
                Name = subscription.Name,
                Description = subscription.Description,
                Price = subscription.Price,
                PaymentPeriod = subscription.PaymentPeriod,
                NextPaymentDate = subscription.NextPaymentDate,
                NotificationDaysBefore = subscription.NotificationDaysBefore,
                IsActive = subscription.IsActive,
                UserId = subscription.UserId
            })
            .ToListAsync(ct);
    }

    public Task<SubscriptionModel?> GetSubscriptionById(Guid subscriptionId, CancellationToken ct)
    {
        return context.Subscriptions
            .AsNoTracking()
            .Where(subscription => subscription.Id == subscriptionId)
            .Select(subscription => new SubscriptionModel
            {
                Id = subscription.Id,
                Name = subscription.Name,
                Description = subscription.Description,
                Price = subscription.Price,
                PaymentPeriod = subscription.PaymentPeriod,
                NextPaymentDate = subscription.NextPaymentDate,
                NotificationDaysBefore = subscription.NotificationDaysBefore,
                IsActive = subscription.IsActive,
                UserId = subscription.UserId
            })
            .FirstOrDefaultAsync(ct);
    }

    public async Task<bool> UpdateSubscription(Guid subscriptionId, SubscriptionModel subscription, CancellationToken ct)
    {
        var updatedRows = await context.Subscriptions
            .Where(entity => entity.Id == subscriptionId)
            .ExecuteUpdateAsync(setters => setters
                    .SetProperty(entity => entity.Name, subscription.Name)
                    .SetProperty(entity => entity.Description, subscription.Description)
                    .SetProperty(entity => entity.Price, subscription.Price)
                    .SetProperty(entity => entity.PaymentPeriod, subscription.PaymentPeriod)
                    .SetProperty(entity => entity.NextPaymentDate, subscription.NextPaymentDate)
                    .SetProperty(
                        entity => entity.NotificationDaysBefore,
                        subscription.NotificationDaysBefore)
                    .SetProperty(entity => entity.IsActive, subscription.IsActive)
                    .SetProperty(entity => entity.UserId, subscription.UserId),
                ct);

        return updatedRows > 0;
    }

    public async Task<bool> DeleteSubscription(Guid subscriptionId, CancellationToken ct)
    {
        var deletedRows = await context.Subscriptions
            .Where(subscription => subscription.Id == subscriptionId)
            .ExecuteDeleteAsync(ct);

        return deletedRows > 0;
    }
}
