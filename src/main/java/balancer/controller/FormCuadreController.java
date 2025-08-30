package balancer.controller;

import balancer.model.Cuadre;
import balancer.service.CuadreService;

/**
 * Controller responsible for persisting cuadre information coming
 * from the form view controller.
 */
public class FormCuadreController {
    private final CuadreService service = new CuadreService();

    /**
     * Persist the given cuadre entity.
     *
     * @param cuadre entity to save
     */
    public void guardar(Cuadre cuadre){
        service.guardar(cuadre);
    }
}
