namespace API.Contracts.Requests;

public record CreateSubscriptionRequest(
    Guid UserId,
    string Name,
    string? Description,
    decimal Price,
    string PaymentPeriod,
    DateOnly NextPaymentDate,
    int? NotificationDaysBefore,
    bool? IsActive
);
