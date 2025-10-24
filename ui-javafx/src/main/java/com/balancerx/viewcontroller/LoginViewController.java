package com.balancerx.viewcontroller;

import com.balancerx.controller.UsuarioController;
import com.balancerx.model.entity.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Controlador de vista para la pantalla de login.
 */
public class LoginViewController {

    @FXML
    private TextField txtEmail;
    
    @FXML
    private PasswordField txtPassword;
    
    @FXML
    private Button btnLogin;
    
    private UsuarioController usuarioController;
    
    /**
     * Inicializa el controlador de vista.
     * @param usuarioController Controlador de usuarios
     */
    public void initialize(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
        
        // Configurar eventos (el FXML ya enlaza onAction="#handleLogin")
        // Si no se usa onAction en FXML, esta línea mantiene el comportamiento.
        btnLogin.setOnAction(event -> handleLogin());
    }
    
    /**
     * Maneja el evento de login.
     */
    @FXML
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error de validación", "Por favor, complete todos los campos.");
            return;
        }
        
        try {
            Optional<Usuario> usuarioOpt = usuarioController.autenticarUsuario(email, password);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                if (!usuario.isActivo()) {
                    mostrarAlerta("Cuenta inactiva", "Su cuenta está desactivada. Contacte al administrador.");
                    return;
                }
                
                // Navegar a la pantalla principal según el rol
                navegarAMenuPrincipal(usuario);
            } else {
                mostrarAlerta("Error de autenticación", "Credenciales incorrectas. Intente nuevamente.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error al intentar iniciar sesión: " + e.getMessage());
        }
    }
    
    /**
     * Navega a la pantalla principal según el rol del usuario.
     * @param usuario Usuario autenticado
     */
    private void navegarAMenuPrincipal(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/balancerx/view/MenuPrincipal.fxml"));
            Parent root = loader.load();
            
            MenuPrincipalViewController controller = loader.getController();
            controller.inicializar(usuario);
            
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            // Adjuntar hoja de estilos global
            scene.getStylesheets().add(getClass().getResource("/com/balancerx/view/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BalancerX - Menú Principal");
            stage.centerOnScreen();
            
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la pantalla principal: " + e.getMessage());
        }
    }
    
    /**
     * Muestra una alerta con el título y mensaje especificados.
     * @param titulo Título de la alerta
     * @param mensaje Mensaje de la alerta
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}