package balancer.controller;

import balancer.model.Cuadre;
import balancer.service.CuadreService;
import java.util.List;

/**
 * Controller for operations related to listing cuadre records.
 * Acts as intermediary between view layer and CuadreService.
 */
public class CuadresController {
    private final CuadreService service = new CuadreService();

    /**
     * List all cuadre records for a given point of sale.
     *
     * @param puntoId identifier of the point of sale
     * @return list of cuadre records
     */
    public List<Cuadre> listarPorPunto(String puntoId){
        return service.listarPorPunto(puntoId);
    }
}
