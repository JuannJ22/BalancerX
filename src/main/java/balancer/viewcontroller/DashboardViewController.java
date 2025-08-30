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
import javafx.scene.control.MenuButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.util.List;

public class DashboardViewController {
    @FXML private FlowPane contenedorCards;
    @FXML private MenuButton userMenu;
    @FXML private Label totalPuntosLabel;
    private final PuntoVentaService pvs = new PuntoVentaService();

    @FXML public void initialize(){
        contenedorCards.setAlignment(Pos.CENTER);
        contenedorCards.setHgap(20); contenedorCards.setVgap(20);
        List<PuntoVenta> puntos = pvs.listar();
        totalPuntosLabel.setText(String.valueOf(puntos.size()));
        if(Sesion.getUsuarioActual() != null){
            userMenu.setText(Sesion.getUsuarioActual().getNombre());
        }
        for(PuntoVenta pv: puntos){
            VBox card = new VBox(10);
            card.getStyleClass().add("card");
            card.setAlignment(Pos.CENTER); card.setPadding(new Insets(20));
            Label nombre = new Label(pv.getNombre());
            nombre.getStyleClass().add("card-label");
            Button btn = new Button("Cuadres");
            btn.getStyleClass().add("card-button");
            btn.setOnAction(e -> {
                Sesion.setPuntoSeleccionado(pv);
                Navigator.navigateTo("cuadres.fxml","Cuadres - "+pv.getNombre());
            });
            card.getChildren().addAll(nombre, btn);
            contenedorCards.getChildren().add(card);
        }
    }

    @FXML public void abrirUsuarios(){ Navigator.navigateTo("usuarios.fxml", "Usuarios - Balancer"); }

    @FXML public void cerrarSesion(){
        Sesion.clear();
        Navigator.navigateTo("login.fxml", "Login - Balancer");
    }
}
