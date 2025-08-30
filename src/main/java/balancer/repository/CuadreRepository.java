package balancer.repository;

import balancer.model.Cuadre;
import java.util.List;

public interface CuadreRepository {
    List<Cuadre> listarPorPunto(String puntoId);
    void guardar(Cuadre c);
}
