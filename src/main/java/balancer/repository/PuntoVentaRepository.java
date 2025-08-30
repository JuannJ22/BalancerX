package balancer.repository;

import balancer.model.PuntoVenta;
import java.util.List;
import java.util.Optional;

public interface PuntoVentaRepository {
    List<PuntoVenta> listar();
    Optional<PuntoVenta> buscarPorId(String id);
    void guardar(PuntoVenta pv);
}
