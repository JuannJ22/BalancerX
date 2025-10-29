package com.balancerx.viewcontroller;

import com.balancerx.AppContext;
import com.balancerx.controller.CuadreController;
import com.balancerx.controller.PuntoVentaController;
import com.balancerx.controller.UsuarioController;
import com.balancerx.model.entity.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Controlador de vista para la pantalla principal del sistema.
 */
public class MenuPrincipalViewController {

    @FXML
    private BorderPane mainContainer;
    
    @FXML
    private Label lblUsuario;
    
    @FXML
    private Label lblRol;
    
    @FXML
    private Button btnCuadres;
    
    @FXML
    private Button btnPuntosVenta;
    
    @FXML
    private Button btnUsuarios;
    
    @FXML
    private Button btnCerrarSesion;
    
    // Nuevos elementos del dashboard
    @FXML
    private Label lblCuadresHoy;
    
    @FXML
    private Label lblPuntosVenta;
    
    @FXML
    private Label lblUsuarios;
    
    @FXML
    private Button btnNuevoCuadre;
    
    @FXML
    private Button btnReportes;
    
    @FXML
    private Button btnConfiguracion;

    private Usuario usuarioActual;
    private AppContext appContext;
    private UsuarioController usuarioController;
    private PuntoVentaController puntoVentaController;
    private CuadreController cuadreController;

    @FXML
    private void initialize() {
        setAppContext(AppContext.getInstance());
    }

    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        this.usuarioController = appContext.getUsuarioController();
        this.puntoVentaController = appContext.getPuntoVentaController();
        this.cuadreController = appContext.getCuadreController();
    }

    /**
     * Inicializa el controlador con el usuario autenticado.
     * @param usuario Usuario autenticado
     */
    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        
        // Configurar la interfaz según el usuario
        lblUsuario.setText(usuario.getNombre());
        lblRol.setText(usuario.getRol());
        
        // Configurar visibilidad de botones según el rol
        configurarPermisosUI(usuario.getRol());
        
        // Configurar eventos de navegación
        btnCuadres.setOnAction(event -> mostrarModuloCuadres());
        btnPuntosVenta.setOnAction(event -> mostrarModuloPuntosVenta());
        btnUsuarios.setOnAction(event -> mostrarModuloUsuarios());
        btnCerrarSesion.setOnAction(event -> cerrarSesion());
        
        // Configurar eventos del dashboard
        btnNuevoCuadre.setOnAction(event -> mostrarModuloCuadres());
        btnReportes.setOnAction(event -> mostrarReportes());
        btnConfiguracion.setOnAction(event -> mostrarConfiguracion());

        // Actualizar estadísticas del dashboard
        actualizarEstadisticasDashboard();
    }
    
    /**
     * Configura la visibilidad de elementos de la UI según el rol del usuario.
     * @param rol Rol del usuario
     */
    private void configurarPermisosUI(String rol) {
        switch (rol) {
            case "ADMIN":
                btnCuadres.setVisible(true);
                btnPuntosVenta.setVisible(true);
                btnUsuarios.setVisible(true);
                break;
            case "ELABORADOR":
                btnCuadres.setVisible(true);
                btnPuntosVenta.setVisible(false);
                btnUsuarios.setVisible(false);
                break;
            case "BANCO":
            case "ASIGNADOR":
                btnCuadres.setVisible(true);
                btnPuntosVenta.setVisible(false);
                btnUsuarios.setVisible(false);
                break;
            case "AUDITOR":
                btnCuadres.setVisible(true);
                btnPuntosVenta.setVisible(false);
                btnUsuarios.setVisible(false);
                break;
            default:
                btnCuadres.setVisible(false);
                btnPuntosVenta.setVisible(false);
                btnUsuarios.setVisible(false);
                break;
        }
    }
    
    /**
     * Muestra el módulo de cuadres.
     */
    private void mostrarModuloCuadres() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/balancerx/view/CuadresView.fxml"));
            Parent view = loader.load();

            CuadresViewController controller = loader.getController();
            controller.setAppContext(appContext);
            controller.inicializar(usuarioActual);

            mainContainer.setCenter(view);
            marcarBotonActivo(btnCuadres);
        } catch (IOException e) {
            mostrarError("Error al cargar el módulo de cuadres", e.getMessage());
        }
    }
    
    /**
     * Muestra el módulo de puntos de venta.
     */
    private void mostrarModuloPuntosVenta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/balancerx/view/PuntosVentaView.fxml"));
            Parent view = loader.load();

            PuntosVentaViewController controller = loader.getController();
            controller.setAppContext(appContext);
            controller.inicializar(usuarioActual);

            mainContainer.setCenter(view);
            marcarBotonActivo(btnPuntosVenta);
        } catch (IOException e) {
            mostrarError("Error al cargar el módulo de puntos de venta", e.getMessage());
        }
    }
    
    /**
     * Muestra el módulo de usuarios.
     */
    private void mostrarModuloUsuarios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/balancerx/view/UsuariosView.fxml"));
            Parent view = loader.load();

            UsuariosViewController controller = loader.getController();
            controller.setAppContext(appContext);
            controller.inicializar(usuarioActual);

            mainContainer.setCenter(view);
            marcarBotonActivo(btnUsuarios);
        } catch (IOException e) {
            mostrarError("Error al cargar el módulo de usuarios", e.getMessage());
        }
    }
    
    /**
     * Cierra la sesión actual y vuelve a la pantalla de login.
     */
    private void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/balancerx/view/Login.fxml"));
            Parent root = loader.load();

            LoginViewController loginController = loader.getController();
            loginController.setUsuarioController(usuarioController);

            Stage stage = (Stage) mainContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            // Adjuntar hoja de estilos global
            scene.getStylesheets().add(getClass().getResource("/com/balancerx/view/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BalancerX - Login");
            stage.centerOnScreen();
            
        } catch (IOException e) {
            mostrarError("Error al cerrar sesión", e.getMessage());
        }
    }
    
    /**
     * Actualiza las estadísticas mostradas en el dashboard.
     */
    private void actualizarEstadisticasDashboard() {
        int cuadresHoy = (int) cuadreController.obtenerTodos().stream()
                .filter(c -> LocalDate.now().equals(c.getFecha()))
                .count();
        int totalPuntos = puntoVentaController.obtenerTodos().size();
        int totalUsuarios = usuarioController.obtenerTodosLosUsuarios().size();

        lblCuadresHoy.setText(String.valueOf(cuadresHoy));
        lblPuntosVenta.setText(String.valueOf(totalPuntos));
        lblUsuarios.setText(String.valueOf(totalUsuarios));
    }
    
    /**
     * Muestra el módulo de reportes.
     */
    private void mostrarReportes() {
        // Implementación futura para reportes
        mostrarInfo("Reportes", "Módulo de reportes en desarrollo");
    }
    
    /**
     * Muestra el módulo de configuración.
     */
    private void mostrarConfiguracion() {
        // Implementación futura para configuración
        mostrarInfo("Configuración", "Módulo de configuración en desarrollo");
    }
    
    /**
     * Muestra un mensaje informativo.
     * @param titulo Título del mensaje
     * @param mensaje Contenido del mensaje
     */
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de error.
     * @param titulo Título del error
     * @param mensaje Mensaje del error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void marcarBotonActivo(Button botonActivo) {
        btnCuadres.getStyleClass().remove("active");
        btnPuntosVenta.getStyleClass().remove("active");
        btnUsuarios.getStyleClass().remove("active");

        if (!botonActivo.getStyleClass().contains("active")) {
            botonActivo.getStyleClass().add("active");
        }
    }
}