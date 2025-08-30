package balancer.viewcontroller;

import balancer.controller.DashboardController;
import balancer.model.PuntoVenta;
import balancer.model.Usuario;
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
    private final DashboardController controller = new DashboardController();

    @FXML public void initialize(){
        contenedorCards.setAlignment(Pos.CENTER);
        contenedorCards.setHgap(20); contenedorCards.setVgap(20);
        List<PuntoVenta> puntos = controller.listarPuntos();
        totalPuntosLabel.setText(String.valueOf(puntos.size()));
        Usuario actual = controller.usuarioActual();
        if(actual != null){
            userMenu.setText(actual.getNombre());
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
                // No se gestiona en el controller porque es navegación específica
                // del cuadro seleccionado; la sesión mantiene el punto actual.
                // Se puede extraer a un controller en futuras funcionalidades.
                // Por ahora se mantiene la lógica existente para navegación.
                Sesion.setPuntoSeleccionado(pv);
                Navigator.navigateTo("cuadres.fxml","Cuadres - "+pv.getNombre());
            });
            card.getChildren().addAll(nombre, btn);
            contenedorCards.getChildren().add(card);
        }
    }

    @FXML public void abrirUsuarios(){ Navigator.navigateTo("usuarios.fxml", "Usuarios - Balancer"); }

    @FXML public void cerrarSesion(){
        controller.cerrarSesion();
        Navigator.navigateTo("login.fxml", "Login - Balancer");
    }
}
