package balancer.util;

import balancer.model.PuntoVenta;
import balancer.model.Usuario;

public final class Sesion {
    private static Usuario usuarioActual;
    private static PuntoVenta puntoSeleccionado;
    private Sesion(){}
    public static Usuario getUsuarioActual(){ return usuarioActual; }
    public static void setUsuarioActual(Usuario u){ usuarioActual = u; }
    public static PuntoVenta getPuntoSeleccionado(){ return puntoSeleccionado; }
    public static void setPuntoSeleccionado(PuntoVenta p){ puntoSeleccionado = p; }
    public static void clear(){
        usuarioActual = null;
        puntoSeleccionado = null;
    }
}
