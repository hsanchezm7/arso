namespace ValoracionesApi.Common;

public enum RolEvaluador
{
    Comprador,
    Vendedor
}

public static class RolesEvaluador
{
    public const string RolComprador = "comprador";
    public const string RolVendedor = "vendedor";
    public const string Patron = "^(" + RolComprador + "|" + RolVendedor + ")$";
}
