package balancer.controller;

import balancer.model.Permiso;
import balancer.model.Usuario;
import balancer.security.AuthorizationService;
import balancer.service.UsuarioService;
import balancer.util.Sesion;
import java.util.List;

/**
 * Controller for user management operations, enforcing authorization
 * rules and delegating persistence to UsuarioService.
 */
public class UsuariosController {
    private final UsuarioService service = new UsuarioService();

    /**
     * Determines if the current session user has permissions to manage users.
     *
     * @return true when user management is allowed
     */
    public boolean puedeGestionar(){
        return AuthorizationService.puede(Sesion.getUsuarioActual(), Permiso.GESTION_USUARIOS);
    }

    /**
     * Retrieve all registered users.
     *
     * @return list of usuarios
     */
    public List<Usuario> listar(){
        return service.listar();
    }

    /**
     * Adds a new user to the system.
     *
     * @param username username
     * @param password password in plain text
     * @param nombre   human-readable name
     * @param rol      role as string
     */
    public void agregar(String username, String password, String nombre, String rol){
        service.agregar(username, password, nombre, rol);
    }
}
