package com.balancerx;

import com.balancerx.controller.CuadreController;
import com.balancerx.controller.PuntoVentaController;
import com.balancerx.controller.UsuarioController;
import com.balancerx.model.service.CuadreService;
import com.balancerx.model.service.PuntoVentaService;
import com.balancerx.model.service.UsuarioService;
import com.balancerx.model.service.impl.CuadreServiceImpl;
import com.balancerx.model.service.impl.PuntoVentaServiceImpl;
import com.balancerx.model.service.impl.UsuarioServiceImpl;

/**
 * Contenedor sencillo de servicios y controladores para la UI JavaFX.
 * Permite reutilizar instancias en toda la aplicación sin depender
 * de un framework de inyección de dependencias completo.
 */
public final class AppContext {

    private static AppContext instance;

    private final UsuarioController usuarioController;
    private final PuntoVentaController puntoVentaController;
    private final CuadreController cuadreController;

    private AppContext() {
        UsuarioService usuarioService = new UsuarioServiceImpl();
        PuntoVentaService puntoVentaService = new PuntoVentaServiceImpl();
        CuadreService cuadreService = new CuadreServiceImpl();

        this.usuarioController = new UsuarioController(usuarioService);
        this.puntoVentaController = new PuntoVentaController(puntoVentaService);
        this.cuadreController = new CuadreController(cuadreService);
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public UsuarioController getUsuarioController() {
        return usuarioController;
    }

    public PuntoVentaController getPuntoVentaController() {
        return puntoVentaController;
    }

    public CuadreController getCuadreController() {
        return cuadreController;
    }
}
