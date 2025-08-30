package balancer.service;

import balancer.model.PuntoVenta;
import balancer.repository.PuntoVentaRepository;
import balancer.repository.impl.RepositoryFactory;
import java.util.List;

public class PuntoVentaService {
    private final PuntoVentaRepository repo = RepositoryFactory.puntos();
    public PuntoVentaService(){
        if(repo.listar().isEmpty()){
            repo.guardar(new PuntoVenta("1","Sucursal Norte"));
            repo.guardar(new PuntoVenta("2","Sucursal Centro"));
            repo.guardar(new PuntoVenta("3","Sucursal Sur"));
            repo.guardar(new PuntoVenta("4","Sucursal Occidente"));
        }
    }
    public List<PuntoVenta> listar(){ return repo.listar(); }
    public void guardar(PuntoVenta p){ repo.guardar(p); }
    public PuntoVentaRepository repository(){ return repo; }
}
