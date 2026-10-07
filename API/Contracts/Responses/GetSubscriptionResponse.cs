using API.Models;

namespace API.Contracts.Responses;

public record GetSubscriptionResponse(
    SubscriptionModel Subscription
    );
