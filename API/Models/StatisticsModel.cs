namespace API.Models;

public class StatisticsModel
{
    public Guid UserId { get; set; }
    public int TotalSubscriptions { get; set; }
    public int ActiveSubscriptions { get; set; }
    public int InactiveSubscriptions { get; set; }
    public decimal MonthlyExpenses { get; set; }
    public decimal YearlyExpenses { get; set; }
    public int PaymentsNextSevenDays { get; set; }
    public decimal PaymentsAmountNextSevenDays { get; set; }
    public DateOnly? NearestPaymentDate { get; set; }
    public string? MostExpensiveSubscriptionName { get; set; }
    public decimal? MostExpensiveSubscriptionMonthlyCost { get; set; }
    public DateTime LastUpdated { get; set; }
}
