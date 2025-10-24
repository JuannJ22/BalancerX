package com.balancerx.viewcontroller;

import com.balancerx.model.entity.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador de vista para la pantalla principal del sistema.
 */
public class MenuPrincipalViewController {

    @FXML
    private BorderPane mainContainer;
    
    @FXML
    private Label lblUsuarioActual;
    
    @FXML
    private Button btnCuadres;
    
    @FXML
    private Button btnPuntosVenta;
    
    @FXML
    private Button btnUsuarios;
    
    @FXML
    private Button btnCerrarSesion;
    
    private Usuario usuarioActual;
    
    /**
     * Inicializa el controlador con el usuario autenticado.
     * @param usuario Usuario autenticado
     */
    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        
        // Configurar la interfaz según el usuario
        lblUsuarioActual.setText("Usuario: " + usuario.getNombre() + " (" + usuario.getRol() + ")");
        
        // Configurar visibilidad de botones según el rol
        configurarPermisosUI(usuario.getRol());
        
        // Configurar eventos
        btnCuadres.setOnAction(event -> mostrarModuloCuadres());
        btnPuntosVenta.setOnAction(event -> mostrarModuloPuntosVenta());
        btnUsuarios.setOnAction(event -> mostrarModuloUsuarios());
        btnCerrarSesion.setOnAction(event -> cerrarSesion());
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
            controller.inicializar(usuarioActual);
            
            mainContainer.setCenter(view);
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
            controller.inicializar(usuarioActual);
            
            mainContainer.setCenter(view);
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
            controller.inicializar(usuarioActual);
            
            mainContainer.setCenter(view);
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
     * Muestra un mensaje de error.
     * @param titulo Título del error
     * @param mensaje Mensaje del error
     */
    private void mostrarError(String titulo, String mensaje) {
        System.err.println(titulo + ": " + mensaje);
        // Aquí se podría mostrar un diálogo de error
    }
}