package balancer.viewcontroller;

import balancer.controller.CuadresController;
import balancer.model.Cuadre;
import balancer.util.Navigator;
import balancer.util.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CuadresViewController {
    @FXML private Label lblTitulo;
    @FXML private TableView<Cuadre> tabla;
    @FXML private TableColumn<Cuadre, String> colFecha;
    @FXML private TableColumn<Cuadre, String> colMonto;
    @FXML private TableColumn<Cuadre, Void> colAcciones;
    private final CuadresController controller = new CuadresController();
    private final ObservableList<Cuadre> datos = FXCollections.observableArrayList();
    @FXML public void initialize(){
        String nombre = Sesion.getPuntoSeleccionado()!=null ? Sesion.getPuntoSeleccionado().getNombre() : "Sin Punto";
        lblTitulo.setText("Cuadres de " + nombre);
        colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        colMonto.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.format("$ %, .2f", c.getValue().getMonto())));
        agregarAcciones();
        if(Sesion.getPuntoSeleccionado()!=null){
            datos.setAll(controller.listarPorPunto(Sesion.getPuntoSeleccionado().getId()));
        }
        tabla.setItems(datos);
    }
    private void agregarAcciones(){
        Callback<TableColumn<Cuadre, Void>, TableCell<Cuadre, Void>> factory = (param) -> new TableCell<>(){
            private final Button btn = new Button("Editar");
            { btn.getStyleClass().add("boton-accion"); btn.setOnAction(e -> {
                Cuadre c = getTableView().getItems().get(getIndex());
                FormCuadreViewController ctrl = Navigator.navigateTo("form_cuadre.fxml","Editar Cuadre");
                ctrl.cargarCuadre(c);
            }); }
            @Override protected void updateItem(Void item, boolean empty){
                super.updateItem(item, empty);
                setGraphic(empty?null:btn);
            }
        };
        colAcciones.setCellFactory(factory);
    }
    @FXML public void nuevoCuadre(){
        Cuadre nuevo = Cuadre.builder().id(UUID.randomUUID().toString())
                .puntoVentaId(Sesion.getPuntoSeleccionado().getId())
                .fecha(java.time.LocalDate.now()).monto(0.0).build();
        FormCuadreViewController ctrl = Navigator.navigateTo("form_cuadre.fxml","Nuevo Cuadre");
        ctrl.cargarCuadre(nuevo);
    }
    @FXML public void volver(){ Navigator.navigateTo("dashboard.fxml","Dashboard - Balancer"); }
}
