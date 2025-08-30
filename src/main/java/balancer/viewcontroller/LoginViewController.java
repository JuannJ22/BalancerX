package balancer.viewcontroller;

import balancer.model.Usuario;
import balancer.service.AuthService;
import balancer.util.Navigator;
import balancer.util.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginViewController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    private final AuthService auth = new AuthService();
    @FXML private void onLogin(){
        Usuario u = auth.login(txtUsuario.getText(), txtPassword.getText());
        if(u!=null){ Sesion.setUsuarioActual(u); Navigator.navigateTo("dashboard.fxml", "Dashboard - Balancer"); }
        else{ lblError.setText("Credenciales inválidas"); }
    }
}
