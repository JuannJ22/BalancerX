package balancer.security;

import balancer.model.Permiso;
import balancer.model.Rol;
import balancer.model.Usuario;

public final class AuthorizationService {
    private AuthorizationService(){}
    public static boolean puede(Usuario u, Permiso p){
        if(u==null) return false;
        Rol r = Rol.valueOf(u.getRol().toUpperCase());
        return r.tiene(p);
    }
}
