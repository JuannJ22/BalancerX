package com.balancerx.viewcontroller;

import com.balancerx.AppContext;
import com.balancerx.controller.PuntoVentaController;
import com.balancerx.model.entity.PuntoVenta;
import com.balancerx.model.entity.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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

    @FXML
    private Button btnEliminar;

    private PuntoVentaController puntoVentaController;
    private ObservableList<PuntoVenta> puntosVentaList;
    private ObservableList<PuntoVenta> puntosVentaListFiltrada;
    private Usuario usuarioActual;
    private PuntoVenta puntoVentaSeleccionado;
    private AppContext appContext;

    /**
     * Inicializa el controlador con el usuario autenticado.
     * @param usuario Usuario autenticado
     */
    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        if (puntoVentaController == null) {
            setAppContext(AppContext.getInstance());
        }
        this.puntosVentaList = FXCollections.observableArrayList();
        this.puntosVentaListFiltrada = FXCollections.observableArrayList();

        // Configurar la tabla
        configurarTabla();

        // Cargar datos
        cargarPuntosVenta();

        // Configurar eventos
        configurarEventos();
    }

    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        this.puntoVentaController = appContext.getPuntoVentaController();
    }
    
    /**
     * Configura las columnas de la tabla.
     */
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        tablaPuntosVenta.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                puntoVentaSeleccionado = newSelection;
                mostrarDetallesPuntoVenta(puntoVentaSeleccionado);
                btnEliminar.setDisable(false);
            } else {
                puntoVentaSeleccionado = null;
                btnEliminar.setDisable(true);
            }
        });
    }

    /**
     * Carga los puntos de venta en la tabla.
     */
    private void cargarPuntosVenta() {
        try {
            puntosVentaList.setAll(puntoVentaController.obtenerTodos());
            tablaPuntosVenta.setItems(puntosVentaList);
            tablaPuntosVenta.getSelectionModel().clearSelection();
            actualizarTextoBotonEstado();
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
        btnEliminar.setOnAction(event -> cambiarEstadoPuntoVenta());

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
        actualizarTextoBotonEstado();
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
        puntoVentaSeleccionado = null;
        btnEliminar.setDisable(true);
        actualizarTextoBotonEstado();
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
                PuntoVenta nuevoPuntoVenta = puntoVentaController.guardar(codigo, nombre, direccion, telefono, email, activo);
                puntosVentaList.add(nuevoPuntoVenta);
            } else {
                // Actualizar punto de venta existente
                puntoVentaSeleccionado = puntoVentaController.actualizar(
                        puntoVentaSeleccionado.getId(), codigo, nombre, direccion, telefono, email, activo);
                tablaPuntosVenta.refresh();
                actualizarTextoBotonEstado();
            }

            limpiarFormulario();
            mostrarMensaje("Punto de venta guardado", "El punto de venta ha sido guardado correctamente.");
            cargarPuntosVenta();
        } catch (Exception e) {
            mostrarError("Error al guardar punto de venta", e.getMessage());
        }
    }

    private void cambiarEstadoPuntoVenta() {
        if (puntoVentaSeleccionado == null) {
            mostrarError("Sin selección", "Seleccione un punto de venta para cambiar su estado");
            return;
        }

        boolean nuevoEstado = !puntoVentaSeleccionado.isActivo();

        if (!nuevoEstado) {
            puntoVentaController.desactivar(puntoVentaSeleccionado.getId());
        } else {
            puntoVentaSeleccionado = puntoVentaController.actualizar(
                    puntoVentaSeleccionado.getId(),
                    puntoVentaSeleccionado.getCodigo(),
                    puntoVentaSeleccionado.getNombre(),
                    puntoVentaSeleccionado.getDireccion(),
                    puntoVentaSeleccionado.getTelefono(),
                    puntoVentaSeleccionado.getEmail(),
                    true
            );
        }

        puntoVentaSeleccionado.setActivo(nuevoEstado);
        tablaPuntosVenta.refresh();
        actualizarTextoBotonEstado();
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
                containsIgnoreCase(puntoVenta.getCodigo(), textoBusqueda) ||
                containsIgnoreCase(puntoVenta.getNombre(), textoBusqueda) ||
                containsIgnoreCase(puntoVenta.getDireccion(), textoBusqueda) ||
                containsIgnoreCase(puntoVenta.getTelefono(), textoBusqueda) ||
                containsIgnoreCase(puntoVenta.getEmail(), textoBusqueda);
            
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
        tablaPuntosVenta.refresh();
        puntosVentaListFiltrada.clear();
        actualizarTextoBotonEstado();
    }

    private void actualizarTextoBotonEstado() {
        if (btnEliminar == null) {
            return;
        }

        if (puntoVentaSeleccionado == null) {
            btnEliminar.setText("Desactivar");
            return;
        }

        btnEliminar.setText(puntoVentaSeleccionado.isActivo() ? "Desactivar" : "Activar");
    }

    private boolean containsIgnoreCase(String value, String search) {
        if (value == null) {
            return false;
        }
        return value.toLowerCase().contains(search);
    }
}
