package balancer.viewcontroller;

import balancer.controller.FormCuadreController;
import balancer.model.Cuadre;
import balancer.util.Navigator;
import balancer.util.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FormCuadreViewController {
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtMonto;
    @FXML private TextArea txtObs;
    @FXML private Label lblTitulo;
    private final FormCuadreController controller = new FormCuadreController();
    private Cuadre actual;
    public void cargarCuadre(Cuadre c){
        this.actual = c;
        lblTitulo.setText("Cuadre - " + (Sesion.getPuntoSeleccionado()!=null?Sesion.getPuntoSeleccionado().getNombre():""));
        dpFecha.setValue(c.getFecha());
        txtMonto.setText(String.valueOf(c.getMonto()));
        txtObs.setText(c.getObservacion()!=null?c.getObservacion():"");
    }
    @FXML public void guardar(){
        try{
            actual.setFecha(dpFecha.getValue());
            actual.setMonto(Double.parseDouble(txtMonto.getText()));
            actual.setObservacion(txtObs.getText());
            controller.guardar(actual);
            Navigator.navigateTo("cuadres.fxml","Cuadres - " + Sesion.getPuntoSeleccionado().getNombre());
        }catch(Exception e){ new Alert(Alert.AlertType.ERROR, "Error: "+e.getMessage()).showAndWait(); }
    }
    @FXML public void cancelar(){ Navigator.navigateTo("cuadres.fxml","Cuadres - " + Sesion.getPuntoSeleccionado().getNombre()); }
}
