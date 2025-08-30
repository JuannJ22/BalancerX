package balancer.controller;

import balancer.model.PuntoVenta;
import balancer.model.Usuario;
import balancer.service.PuntoVentaService;
import balancer.util.Sesion;
import java.util.List;

/**
 * Controller for dashboard operations. Retrieves data required by the
 * dashboard view controllers and manages session information.
 */
public class DashboardController {
    private final PuntoVentaService pvs = new PuntoVentaService();

    /**
     * Fetch all registered points of sale.
     *
     * @return list of PuntoVenta
     */
    public List<PuntoVenta> listarPuntos(){
        return pvs.listar();
    }

    /**
     * Returns the current authenticated user stored in the session.
     *
     * @return Usuario or null
     */
    public Usuario usuarioActual(){
        return Sesion.getUsuarioActual();
    }

    /**
     * Clears the session information, effectively logging out.
     */
    public void cerrarSesion(){
        Sesion.clear();
    }
}
