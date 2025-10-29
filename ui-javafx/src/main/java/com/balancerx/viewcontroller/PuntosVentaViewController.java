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
    private TableColumn<PuntoVenta, String> colCodigo;
    
    @FXML
    private TableColumn<PuntoVenta, String> colNombre;
    
    @FXML
    private TableColumn<PuntoVenta, String> colDireccion;
    
    @FXML
    private TableColumn<PuntoVenta, String> colTelefono;
    
    @FXML
    private TableColumn<PuntoVenta, Boolean> colActivo;
    
    @FXML
    private TextField txtCodigo;
    
    @FXML
    private TextField txtNombre;
    
    @FXML
    private TextField txtDireccion;
    
    @FXML
    private TextField txtTelefono;
    
    @FXML
    private TextField txtEmail;
    
    @FXML
    private TextField txtBuscar;
    
    @FXML
    private CheckBox chkActivo;
    
    @FXML
    private Button btnGuardar;
    
    @FXML
    private Button btnNuevo;
    
    @FXML
    private Button btnBuscar;
    
    @FXML
    private Button btnLimpiarBusqueda;
    
    private PuntoVentaController puntoVentaController;
    private ObservableList<PuntoVenta> puntosVentaList;
    private ObservableList<PuntoVenta> puntosVentaListFiltrada;
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
        this.puntosVentaListFiltrada = FXCollections.observableArrayList();
        
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
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
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
            puntosVentaList.add(new PuntoVenta(1L, "PV001", "Punto de Venta Centro", "Calle 123 #45-67", "555-0001", "centro@balancerx.com", true, LocalDateTime.now()));
            puntosVentaList.add(new PuntoVenta(2L, "PV002", "Punto de Venta Norte", "Carrera 89 #12-34", "555-0002", "norte@balancerx.com", true, LocalDateTime.now()));
            puntosVentaList.add(new PuntoVenta(3L, "PV003", "Punto de Venta Sur", "Avenida 56 #78-90", "555-0003", "sur@balancerx.com", false, LocalDateTime.now()));
            
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
        btnBuscar.setOnAction(event -> buscarPuntosVenta());
        btnLimpiarBusqueda.setOnAction(event -> limpiarBusqueda());
        
        // Búsqueda en tiempo real
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                limpiarBusqueda();
            }
        });
    }
    
    /**
     * Muestra los detalles del punto de venta seleccionado.
     * @param puntoVenta Punto de venta seleccionado
     */
    private void mostrarDetallesPuntoVenta(PuntoVenta puntoVenta) {
        txtCodigo.setText(puntoVenta.getCodigo());
        txtNombre.setText(puntoVenta.getNombre());
        txtDireccion.setText(puntoVenta.getDireccion());
        txtTelefono.setText(puntoVenta.getTelefono());
        txtEmail.setText(puntoVenta.getEmail());
        chkActivo.setSelected(puntoVenta.isActivo());
    }
    
    /**
     * Limpia el formulario para un nuevo punto de venta.
     */
    private void limpiarFormulario() {
        puntoVentaSeleccionado = null;
        txtCodigo.clear();
        txtNombre.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        txtEmail.clear();
        chkActivo.setSelected(true);
        txtCodigo.requestFocus();
    }
    
    /**
     * Guarda el punto de venta actual.
     */
    private void guardarPuntoVenta() {
        try {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
            boolean activo = chkActivo.isSelected();
            
            if (codigo.isEmpty() || nombre.isEmpty()) {
                mostrarError("Error de validación", "El código y nombre del punto de venta son obligatorios");
                return;
            }
            
            if (puntoVentaSeleccionado == null) {
                // Crear nuevo punto de venta
                PuntoVenta nuevoPuntoVenta = new PuntoVenta(null, codigo, nombre, direccion, telefono, email, activo, LocalDateTime.now());
                // En una implementación real, esto guardaría usando el controlador
                // puntoVentaController.guardar(nuevoPuntoVenta);
                
                // Por ahora, simulamos la asignación de un ID
                nuevoPuntoVenta.setId((long) (puntosVentaList.size() + 1));
                puntosVentaList.add(nuevoPuntoVenta);
            } else {
                // Actualizar punto de venta existente
                puntoVentaSeleccionado.setCodigo(codigo);
                puntoVentaSeleccionado.setNombre(nombre);
                puntoVentaSeleccionado.setDireccion(direccion);
                puntoVentaSeleccionado.setTelefono(telefono);
                puntoVentaSeleccionado.setEmail(email);
                puntoVentaSeleccionado.setActivo(activo);
                // En una implementación real, esto actualizaría usando el controlador
                // puntoVentaController.actualizar(puntoVentaSeleccionado);
                
                // Actualizar la tabla
                tablaPuntosVenta.refresh();
            }
            
            limpiarFormulario();
            mostrarMensaje("Punto de venta guardado", "El punto de venta ha sido guardado correctamente.");
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
    
    /**
     * Muestra un mensaje informativo.
     * @param titulo Título del mensaje
     * @param mensaje Contenido del mensaje
     */
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Busca puntos de venta según el texto ingresado.
     */
    private void buscarPuntosVenta() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        
        puntosVentaListFiltrada.clear();
        
        for (PuntoVenta puntoVenta : puntosVentaList) {
            boolean coincide = textoBusqueda.isEmpty() ||
                puntoVenta.getCodigo().toLowerCase().contains(textoBusqueda) ||
                puntoVenta.getNombre().toLowerCase().contains(textoBusqueda) ||
                puntoVenta.getDireccion().toLowerCase().contains(textoBusqueda) ||
                puntoVenta.getTelefono().toLowerCase().contains(textoBusqueda) ||
                puntoVenta.getEmail().toLowerCase().contains(textoBusqueda);
            
            if (coincide) {
                puntosVentaListFiltrada.add(puntoVenta);
            }
        }
        
        tablaPuntosVenta.setItems(puntosVentaListFiltrada);
    }
    
    /**
     * Limpia la búsqueda y muestra todos los puntos de venta.
     */
    private void limpiarBusqueda() {
        txtBuscar.clear();
        tablaPuntosVenta.setItems(puntosVentaList);
    }
}