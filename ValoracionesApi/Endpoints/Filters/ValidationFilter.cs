using System.ComponentModel.DataAnnotations;

namespace ValoracionesApi.Endpoints.Filters;

public class ValidationFilter<T> : IEndpointFilter
{
    public async ValueTask<object?> InvokeAsync(EndpointFilterInvocationContext context, EndpointFilterDelegate next)
    {
        var model = context.Arguments.OfType<T>().FirstOrDefault();
        if (model == null)
        {
            return await next(context);
        }

        var validationResults = new List<ValidationResult>();
        var validationContext = new ValidationContext(model);
        if (Validator.TryValidateObject(model, validationContext, validationResults, true))
        {
            return await next(context);
        }

        var errors = new Dictionary<string, string[]>();
        foreach (var result in validationResults)
        {
            var members = result.MemberNames.Any() ? result.MemberNames : new[] { string.Empty };
            foreach (var member in members)
            {
                var message = result.ErrorMessage ?? "Validation error";
                if (errors.TryGetValue(member, out var existing))
                {
                    errors[member] = existing.Concat(new[] { message }).ToArray();
                }
                else
                {
                    errors[member] = new[] { message };
                }
            }
        }

        return Results.ValidationProblem(errors);
    }
}
