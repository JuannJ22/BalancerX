package balancer.viewcontroller;

import balancer.controller.LoginController;
import balancer.model.Usuario;
import balancer.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.prefs.Preferences;

public class LoginViewController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Label lblError;
    @FXML private CheckBox remember;
    private final LoginController controller = new LoginController();
    private final Preferences prefs = Preferences.userNodeForPackage(LoginViewController.class);

    @FXML public void initialize(){
        String remembered = prefs.get("rememberedUser", "");
        if(!remembered.isEmpty()){
            txtUsuario.setText(remembered);
            remember.setSelected(true);
        }
    }

    @FXML private void onTogglePassword(){
        if(txtPassword.isVisible()){
            txtPasswordVisible.setText(txtPassword.getText());
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
        }else{
            txtPassword.setText(txtPasswordVisible.getText());
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
        }
    }

    @FXML private void onLogin(){
        String pass = txtPassword.isVisible() ? txtPassword.getText() : txtPasswordVisible.getText();
        Usuario u = controller.login(txtUsuario.getText(), pass);
        if(u!=null){
            if(remember.isSelected()){
                prefs.put("rememberedUser", txtUsuario.getText());
            }else{
                prefs.remove("rememberedUser");
            }
            Navigator.navigateTo("dashboard.fxml", "Dashboard - Balancer");
        }else{
            lblError.setText("Credenciales inválidas");
        }
    }
}
