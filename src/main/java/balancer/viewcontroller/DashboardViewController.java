package balancer.viewcontroller;

import balancer.model.PuntoVenta;
import balancer.service.PuntoVentaService;
import balancer.util.Navigator;
import balancer.util.Sesion;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class DashboardViewController {
    @FXML private FlowPane contenedorCards;
    private final PuntoVentaService pvs = new PuntoVentaService();
    @FXML public void initialize(){
        contenedorCards.setAlignment(Pos.CENTER);
        contenedorCards.setHgap(20); contenedorCards.setVgap(20);
        for(PuntoVenta pv: pvs.listar()){
            VBox card = new VBox(10);
            card.getStyleClass().add("card");
            card.setAlignment(Pos.CENTER); card.setPadding(new Insets(20));
            Label nombre = new Label(pv.getNombre()); nombre.getStyleClass().add("card-label");
            Button btn = new Button("Cuadres"); btn.getStyleClass().add("card-button");
            btn.setOnAction(e -> { Sesion.setPuntoSeleccionado(pv); Navigator.navigateTo("cuadres.fxml","Cuadres - "+pv.getNombre()); });
            card.getChildren().addAll(nombre, btn);
            contenedorCards.getChildren().add(card);
        }
    }
    @FXML public void abrirUsuarios(){ Navigator.navigateTo("usuarios.fxml", "Usuarios - Balancer"); }
}
