using API.Contracts.Requests;
using API.Contracts.Responses;
using API.Interfaces.Services;
using API.Models;
using Microsoft.AspNetCore.Mvc;
using Swashbuckle.AspNetCore.Annotations;

namespace API.Controllers;

[ApiController]
[Route("api/[controller]")]
public class SubscriptionsController(ISubscriptionsService subscriptionsService) : ControllerBase
{
    [HttpGet]
    public async Task<ActionResult<GetAllSubscriptionsResponse>> GetAllSubscriptions(CancellationToken ct)
    {
        var subscriptions = await subscriptionsService.GetAllSubscriptions(ct);
        return Ok(new GetAllSubscriptionsResponse(subscriptions));
    }

    [HttpGet("{id:guid}")]
    public async Task<ActionResult<GetSubscriptionResponse>> GetSubscription(Guid id, CancellationToken ct)
    {
        var subscription = await subscriptionsService.GetSubscriptionById(id, ct);
        return subscription is null ? NotFound() : Ok(new GetSubscriptionResponse(subscription));
    }

    [HttpPost]
    public async Task<ActionResult<CreateSubscriptionResponse>> CreateSubscription(
        [FromBody] CreateSubscriptionRequest request,
        CancellationToken ct)
    {
        var subscription = new SubscriptionModel
        {
            UserId = request.UserId,
            Name = request.Name,
            Description = request.Description,
            Price = request.Price,
            PaymentPeriod = request.PaymentPeriod,
            NextPaymentDate = request.NextPaymentDate,
            NotificationDaysBefore = request.NotificationDaysBefore,
            IsActive = request.IsActive
        };

        var createdSubscriptionId = await subscriptionsService.CreateSubscription(subscription, ct);
        return createdSubscriptionId is null ? BadRequest() : Ok(new CreateSubscriptionResponse(createdSubscriptionId.Value));
    }

    [HttpPut("{id:guid}")]
    public async Task<ActionResult<UpdateSubscriptionResponse>> UpdateSubscription(
        Guid id,
        [FromBody] UpdateSubscriptionRequest request,
        CancellationToken ct)
    {
        var subscription = new SubscriptionModel
        {
            Id = id,
            UserId = request.UserId,
            Name = request.Name,
            Description = request.Description,
            Price = request.Price,
            PaymentPeriod = request.PaymentPeriod,
            NextPaymentDate = request.NextPaymentDate,
            NotificationDaysBefore = request.NotificationDaysBefore,
            IsActive = request.IsActive
        };

        var updatedSubscription = await subscriptionsService.UpdateSubscription(id, subscription, ct);

        return updatedSubscription ? Ok(new UpdateSubscriptionResponse(id)) : BadRequest();

    }

    [HttpDelete("{id:guid}")]
    public async Task<ActionResult<DeleteSubscriptionResponse>> DeleteSubscription(Guid id, CancellationToken ct)
    {
        var deleted = await subscriptionsService.DeleteSubscription(id, ct);
        return deleted ? Ok(new DeleteSubscriptionResponse(id)) : BadRequest();
    }
}
