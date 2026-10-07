using API.Models;

namespace API.Contracts.Responses;

public record GetAllSubscriptionsResponse(
    List<SubscriptionModel>
        Subscriptions);
