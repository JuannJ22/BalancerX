package balancer.service;

import balancer.model.Cuadre;
import balancer.repository.CuadreRepository;
import balancer.repository.impl.RepositoryFactory;
import java.util.List;

public class CuadreService {
    private final CuadreRepository repo = RepositoryFactory.cuadres();
    public List<Cuadre> listarPorPunto(String puntoId){ return repo.listarPorPunto(puntoId); }
    public void guardar(Cuadre c){ repo.guardar(c); }
    public CuadreRepository repository(){ return repo; }
}
