package balancer.controller;

import balancer.model.Usuario;
import balancer.service.AuthService;
import balancer.util.Sesion;

/**
 * Controller layer for login operations. Acts as a bridge between
 * the view layer and the authentication services.
 */
public class LoginController {
    private final AuthService auth = new AuthService();

    /**
     * Attempts to authenticate the given user. If successful, the
     * authenticated user is stored in the current session.
     *
     * @param username provided username
     * @param password provided password
     * @return authenticated Usuario or {@code null} if credentials are invalid
     */
    public Usuario login(String username, String password){
        Usuario u = auth.login(username, password);
        if(u != null){
            Sesion.setUsuarioActual(u);
        }
        return u;
    }
}
