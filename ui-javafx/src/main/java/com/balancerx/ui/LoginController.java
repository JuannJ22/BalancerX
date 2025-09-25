package com.balancerx.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label statusLabel;

    @FXML
    void initialize() {
        statusLabel.setText("Ingrese sus credenciales de BalancerX");
    }

    @FXML
    void onLogin(ActionEvent event) {
        if (emailField.getText().isBlank() || passwordField.getText().isBlank()) {
            statusLabel.setText("Usuario y contraseña requeridos");
        } else {
            statusLabel.setText("Autenticando contra API...");
        }
    }
}
