package com.balancerx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación BalancerX.
 * Punto de entrada para la aplicación JavaFX.
 */
public class BalancerXDesktopApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/balancerx/view/Login.fxml"));
        Scene scene = new Scene(loader.load(), 480, 320);

        // Adjuntar hoja de estilos global
        scene.getStylesheets().add(getClass().getResource("/com/balancerx/view/styles.css").toExternalForm());

        stage.setTitle("BalancerX - Sistema de Cuadre de Caja");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true); // Inicia maximizada
        stage.setMinWidth(800);   // Tamaño mínimo
        stage.setMinHeight(600);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}