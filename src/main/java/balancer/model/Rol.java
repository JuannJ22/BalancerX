package balancer.model;

import java.util.Set;

public enum Rol {
    SUPERADMIN(java.util.Set.of(Permiso.values())),
    ADMIN(java.util.Set.of(Permiso.CREAR_CUADRE, Permiso.EDITAR_CUADRE, Permiso.VER_CUADRES)),
    USUARIO(java.util.Set.of(Permiso.VER_CUADRES));
    private final Set<Permiso> permisos;
    Rol(Set<Permiso> permisos){ this.permisos=permisos; }
    public boolean tiene(Permiso p){ return permisos.contains(p); }
}
