package com.balancerx.viewcontroller;

import com.balancerx.controller.PuntoVentaController;
import com.balancerx.model.entity.PuntoVenta;
import com.balancerx.model.entity.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;

/**
 * Controlador de vista para la gestión de puntos de venta.
 */
public class PuntosVentaViewController {

    @FXML
    private TableView<PuntoVenta> tablaPuntosVenta;
    
    @FXML
    private TableColumn<PuntoVenta, Long> colId;
    
    @FXML
    private TableColumn<PuntoVenta, String> colNombre;
    
    @FXML
    private TableColumn<PuntoVenta, Boolean> colActivo;
    
    @FXML
    private TextField txtNombre;
    
    @FXML
    private CheckBox chkActivo;
    
    @FXML
    private Button btnGuardar;
    
    @FXML
    private Button btnNuevo;
    
    private PuntoVentaController puntoVentaController;
    private ObservableList<PuntoVenta> puntosVentaList;
    private Usuario usuarioActual;
    private PuntoVenta puntoVentaSeleccionado;
    
    /**
     * Inicializa el controlador con el usuario autenticado.
     * @param usuario Usuario autenticado
     */
    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        this.puntoVentaController = new PuntoVentaController(new com.balancerx.model.service.impl.PuntoVentaServiceImpl());
        this.puntosVentaList = FXCollections.observableArrayList();
        
        // Configurar la tabla
        configurarTabla();
        
        // Cargar datos
        cargarPuntosVenta();
        
        // Configurar eventos
        configurarEventos();
    }
    
    /**
     * Configura las columnas de la tabla.
     */
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        
        tablaPuntosVenta.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                puntoVentaSeleccionado = newSelection;
                mostrarDetallesPuntoVenta(puntoVentaSeleccionado);
            }
        });
    }
    
    /**
     * Carga los puntos de venta en la tabla.
     */
    private void cargarPuntosVenta() {
        try {
            puntosVentaList.clear();
            // En una implementación real, esto cargaría desde el controlador
            // puntosVentaList.addAll(puntoVentaController.obtenerTodos());
            
            // Por ahora, agregamos datos de ejemplo
            puntosVentaList.add(new PuntoVenta(1L, "Punto de Venta 1", true, LocalDateTime.now()));
            puntosVentaList.add(new PuntoVenta(2L, "Punto de Venta 2", true, LocalDateTime.now()));
            puntosVentaList.add(new PuntoVenta(3L, "Punto de Venta 3", false, LocalDateTime.now()));
            
            tablaPuntosVenta.setItems(puntosVentaList);
        } catch (Exception e) {
            mostrarError("Error al cargar puntos de venta", e.getMessage());
        }
    }
    
    /**
     * Configura los eventos de los botones.
     */
    private void configurarEventos() {
        btnNuevo.setOnAction(event -> limpiarFormulario());
        btnGuardar.setOnAction(event -> guardarPuntoVenta());
    }
    
    /**
     * Muestra los detalles del punto de venta seleccionado.
     * @param puntoVenta Punto de venta seleccionado
     */
    private void mostrarDetallesPuntoVenta(PuntoVenta puntoVenta) {
        txtNombre.setText(puntoVenta.getNombre());
        chkActivo.setSelected(puntoVenta.isActivo());
    }
    
    /**
     * Limpia el formulario para un nuevo punto de venta.
     */
    private void limpiarFormulario() {
        puntoVentaSeleccionado = null;
        txtNombre.clear();
        chkActivo.setSelected(true);
        txtNombre.requestFocus();
    }
    
    /**
     * Guarda el punto de venta actual.
     */
    private void guardarPuntoVenta() {
        try {
            String nombre = txtNombre.getText().trim();
            boolean activo = chkActivo.isSelected();
            
            if (nombre.isEmpty()) {
                mostrarError("Error de validación", "El nombre del punto de venta es obligatorio");
                return;
            }
            
            if (puntoVentaSeleccionado == null) {
                // Crear nuevo punto de venta
                PuntoVenta nuevoPuntoVenta = new PuntoVenta(null, nombre, activo, LocalDateTime.now());
                // En una implementación real, esto guardaría usando el controlador
                // puntoVentaController.guardar(nuevoPuntoVenta);
                
                // Por ahora, simulamos la asignación de un ID
                nuevoPuntoVenta.setId((long) (puntosVentaList.size() + 1));
                puntosVentaList.add(nuevoPuntoVenta);
            } else {
                // Actualizar punto de venta existente
                puntoVentaSeleccionado.setNombre(nombre);
                puntoVentaSeleccionado.setActivo(activo);
                // En una implementación real, esto actualizaría usando el controlador
                // puntoVentaController.actualizar(puntoVentaSeleccionado);
                
                // Actualizar la tabla
                tablaPuntosVenta.refresh();
            }
            
            limpiarFormulario();
        } catch (Exception e) {
            mostrarError("Error al guardar punto de venta", e.getMessage());
        }
    }
    
    /**
     * Muestra un mensaje de error.
     * @param titulo Título del error
     * @param mensaje Mensaje del error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}