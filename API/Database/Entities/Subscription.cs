namespace API.DataBase.Entities;

public partial class Subscription
{
    public Guid Id { get; set; }

    public string Name { get; set; } = null!;

    public string? Description { get; set; }

    public decimal Price { get; set; }

    public string PaymentPeriod { get; set; } = null!;

    public DateOnly NextPaymentDate { get; set; }

    public int? NotificationDaysBefore { get; set; }

    public bool? IsActive { get; set; }

    public Guid UserId { get; set; }

    public virtual User User { get; set; } = null!;
}
