namespace BalancerX.Domain.Constantes;

public static class EstadosTransferencia
{
    public const string SinImprimir = "SIN_IMPRIMIR";
    public const string Impresa = "IMPRESA";

    public static readonly IReadOnlySet<string> Permitidos = new HashSet<string>(StringComparer.OrdinalIgnoreCase)
    {
        SinImprimir,
        Impresa
    };
}
