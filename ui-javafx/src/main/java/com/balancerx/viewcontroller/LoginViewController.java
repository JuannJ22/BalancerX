package com.balancerx.viewcontroller;

import com.balancerx.AppContext;
import com.balancerx.controller.UsuarioController;
import com.balancerx.model.entity.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    @FXML
    private Label lblError;

    private UsuarioController usuarioController;

    /**
     * Inicializa los componentes declarados en el FXML.
     */
    @FXML
    public void initialize() {
        if (usuarioController == null) {
            usuarioController = AppContext.getInstance().getUsuarioController();
        }

        if (lblError != null) {
            lblError.setVisible(false);
            lblError.managedProperty().bind(lblError.visibleProperty());
        }

        btnLogin.setOnAction(event -> handleLogin());
    }

    /**
     * Permite inyectar un {@link UsuarioController} externo (p.e. en pruebas unitarias).
     * @param usuarioController controlador a utilizar
     */
    public void setUsuarioController(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }
    
    /**
     * Maneja el evento de login.
     */
    @FXML
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, complete todos los campos.");
            return;
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioController.autenticarUsuario(email, password);

            if (usuarioOpt.isPresent()) {
                if (lblError != null) {
                    lblError.setVisible(false);
                }

                Usuario usuario = usuarioOpt.get();
                if (!usuario.isActivo()) {
                    mostrarAlerta("Cuenta inactiva", "Su cuenta está desactivada. Contacte al administrador.");
                    return;
                }

                // Navegar a la pantalla principal según el rol
                navegarAMenuPrincipal(usuario);
            } else {
                mostrarError("Credenciales incorrectas. Intente nuevamente.");
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
            controller.setAppContext(AppContext.getInstance());
            controller.inicializar(usuario);

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            // Adjuntar hoja de estilos global
            scene.getStylesheets().add(getClass().getResource("/com/balancerx/view/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BalancerX - Menú Principal");
            stage.setResizable(true);
            stage.setMaximized(true); // Inicia maximizada
            stage.setMinWidth(1000);   // Tamaño mínimo más grande para el menú principal
            stage.setMinHeight(700);
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

    private void mostrarError(String mensaje) {
        if (lblError != null) {
            lblError.setText(mensaje);
            lblError.setVisible(true);
        } else {
            mostrarAlerta("Error", mensaje);
        }
    }
}