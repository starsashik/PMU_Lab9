namespace API.Models;

public class SubscriptionModel
{
    public Guid Id { get; set; }

    public string Name { get; set; } = string.Empty;

    public string? Description { get; set; } = string.Empty;

    public decimal Price { get; set; } = 0;

    public string PaymentPeriod { get; set; } = string.Empty;

    public DateOnly NextPaymentDate { get; set; }

    public int? NotificationDaysBefore { get; set; }

    public bool? IsActive { get; set; }

    public Guid UserId { get; set; }
}
