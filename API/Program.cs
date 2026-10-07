using API.DataBase.Context;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi;
using API.Interfaces.Repositories;
using API.Interfaces.Services;
using API.Middleware;
using API.Repositories;
using API.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();

builder.Services.AddLogging();

builder.Services.AddSwaggerGen(options =>
{
    options.EnableAnnotations();

    options.SwaggerDoc("api", new OpenApiInfo
    {
        Version = "1.0",
        Title = "SubTrack API",
        Description = "API для управления пользователями и подписками."
    });

    options.AddSecurityDefinition("ApiKey", new OpenApiSecurityScheme
    {
        Description = "API-ключ для доступа к SubTrack API",
        Name = "X-API-Key",
        In = ParameterLocation.Header,
        Type = SecuritySchemeType.ApiKey,
        Scheme = "ApiKey"
    });

    options.AddSecurityRequirement(document => new OpenApiSecurityRequirement
    {
        [new OpenApiSecuritySchemeReference("ApiKey", document)] = []
    });
});

builder.Services.AddScoped<IUsersRepository, UsersRepository>();
builder.Services.AddScoped<IUsersService, UsersService>();
builder.Services.AddScoped<ISubscriptionRepository, SubscriptionRepository>();
builder.Services.AddScoped<ISubscriptionsService, SubscriptionsService>();
builder.Services.AddScoped<IAuthorizationService, AuthorizationService>();
builder.Services.AddSingleton<IApiKeyService, ApiKeyService>();
builder.Services.AddScoped<IStatisticsService, StatisticsService>();

builder.Services.AddDbContext<MyDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("MyDbContext")));

builder.Services.AddCors(options => options.AddPolicy(
    "SubTrackApiPolicy", b => b
        .AllowAnyOrigin()
        .AllowAnyHeader()
        .AllowAnyMethod()));

var app = builder.Build();

app.UseCors("SubTrackApiPolicy");

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI(options =>
    {
        options.SwaggerEndpoint("/swagger/api/swagger.json", "SubTrack API");
    });
}

app.UseMiddleware<ApiKeyMiddleware>();

app.UseAuthorization();
app.MapControllers();
app.MapGet("/", () => Results.Redirect("/swagger"));

app.Run();
