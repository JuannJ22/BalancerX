package com.balancerx.viewcontroller;

import com.balancerx.AppContext;
import com.balancerx.controller.CuadreController;
import com.balancerx.controller.PuntoVentaController;
import com.balancerx.model.entity.Cuadre;
import com.balancerx.model.entity.PuntoVenta;
import com.balancerx.model.entity.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de vista para la gestión de cuadres.
 */
public class CuadresViewController {

    @FXML
    private TableView<Cuadre> tablaCuadres;
    
    @FXML
    private TableColumn<Cuadre, Long> colId;
    
    @FXML
    private TableColumn<Cuadre, LocalDate> colFecha;
    
    @FXML
    private TableColumn<Cuadre, String> colPuntoVenta;
    
    @FXML
    private TableColumn<Cuadre, Cuadre.EstadoCuadre> colEstado;
    
    @FXML
    private DatePicker dpFecha;
    
    @FXML
    private ComboBox<String> cbPuntoVenta;
    
    @FXML
    private TextField txtTotalTirilla;
    
    @FXML
    private TextField txtTotalBancos;
    
    @FXML
    private TextField txtTotalContable;
    
    @FXML
    private Button btnGuardar;
    
    @FXML
    private Button btnNuevo;
    
    @FXML
    private TextField txtBuscar;
    
    @FXML
    private ComboBox<String> cbFiltroEstado;
    
    @FXML
    private Button btnBuscar;
    
    @FXML
    private Button btnLimpiarBusqueda;
    
    private CuadreController cuadreController;
    private ObservableList<Cuadre> cuadresList;
    private ObservableList<Cuadre> cuadresListFiltrada;
    private Usuario usuarioActual;
    private Cuadre cuadreSeleccionado;
    private AppContext appContext;
    private PuntoVentaController puntoVentaController;
    private final Map<Long, String> nombresPuntosVenta = new HashMap<>();

    /**
     * Inicializa el controlador con el usuario autenticado.
     * @param usuario Usuario autenticado
     */
    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        if (cuadreController == null) {
            setAppContext(AppContext.getInstance());
        }
        this.cuadresList = FXCollections.observableArrayList();
        this.cuadresListFiltrada = FXCollections.observableArrayList();

        // Configurar la tabla
        configurarTabla();

        // Configurar controles
        configurarControles();

        // Cargar datos
        cargarCuadres();

        // Configurar eventos
        configurarEventos();
    }

    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        this.cuadreController = appContext.getCuadreController();
        this.puntoVentaController = appContext.getPuntoVentaController();
    }
    
    /**
     * Configura las columnas de la tabla.
     */
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colPuntoVenta.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() ->
                nombresPuntosVenta.getOrDefault(cellData.getValue().getPuntoVentaId(),
                        "PV " + cellData.getValue().getPuntoVentaId())));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaCuadres.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cuadreSeleccionado = newSelection;
                mostrarDetallesCuadre(cuadreSeleccionado);
                btnGuardar.setText("Actualizar");
            }
        });
    }

    /**
     * Configura los controles del formulario.
     */
    private void configurarControles() {
        dpFecha.setValue(LocalDate.now());

        cargarPuntosDeVenta();

        // Configurar filtro de estados
        ObservableList<String> estados = FXCollections.observableArrayList(
            "Todos", "BORRADOR", "APROBADO", "RECHAZADO"
        );
        cbFiltroEstado.setItems(estados);
        cbFiltroEstado.getSelectionModel().selectFirst();
    }
    
    /**
     * Carga los cuadres en la tabla.
     */
    private void cargarCuadres() {
        try {
            cuadresList.setAll(cuadreController.obtenerTodos());
            tablaCuadres.setItems(cuadresList);
            tablaCuadres.getSelectionModel().clearSelection();
            btnGuardar.setText("Guardar");
        } catch (Exception e) {
            mostrarError("Error al cargar cuadres", e.getMessage());
        }
    }
    
    /**
     * Configura los eventos de los botones.
     */
    private void configurarEventos() {
        btnNuevo.setOnAction(event -> limpiarFormulario());
        btnGuardar.setOnAction(event -> guardarCuadre());
        btnBuscar.setOnAction(event -> buscarCuadres());
        btnLimpiarBusqueda.setOnAction(event -> limpiarBusqueda());
        
        // Filtrado automático por estado
        cbFiltroEstado.setOnAction(event -> aplicarFiltros());
        
        // Búsqueda en tiempo real
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty() && cbFiltroEstado.getValue().equals("Todos")) {
                limpiarBusqueda();
            }
        });
    }
    
    /**
     * Muestra los detalles del cuadre seleccionado.
     * @param cuadre Cuadre seleccionado
     */
    private void mostrarDetallesCuadre(Cuadre cuadre) {
        dpFecha.setValue(cuadre.getFecha());
        String opcion = cuadre.getPuntoVentaId() + " - " +
                nombresPuntosVenta.getOrDefault(cuadre.getPuntoVentaId(), "Punto de Venta");
        cbPuntoVenta.getSelectionModel().select(opcion);

        txtTotalTirilla.setText(cuadre.getTotalTirilla() != null ? cuadre.getTotalTirilla().toString() : "");
        txtTotalBancos.setText(cuadre.getTotalBancos() != null ? cuadre.getTotalBancos().toString() : "");
        txtTotalContable.setText(cuadre.getTotalContable() != null ? cuadre.getTotalContable().toString() : "");
    }
    
    /**
     * Limpia el formulario para un nuevo cuadre.
     */
    private void limpiarFormulario() {
        cuadreSeleccionado = null;
        dpFecha.setValue(LocalDate.now());
        if (!cbPuntoVenta.getItems().isEmpty()) {
            cbPuntoVenta.getSelectionModel().selectFirst();
        }
        txtTotalTirilla.clear();
        txtTotalBancos.clear();
        txtTotalContable.clear();
        btnGuardar.setText("Guardar");
    }
    
    /**
     * Guarda el cuadre actual.
     */
    private void guardarCuadre() {
        try {
            LocalDate fecha = dpFecha.getValue();
            String puntoVentaStr = cbPuntoVenta.getValue();
            if (puntoVentaStr == null || puntoVentaStr.isBlank()) {
                mostrarError("Error de validación", "Seleccione un punto de venta válido");
                return;
            }
            Long puntoVentaId = Long.parseLong(puntoVentaStr.split(" - ")[0]);
            
            BigDecimal totalTirilla = txtTotalTirilla.getText().isEmpty() ? 
                BigDecimal.ZERO : new BigDecimal(txtTotalTirilla.getText());
            BigDecimal totalBancos = txtTotalBancos.getText().isEmpty() ? 
                BigDecimal.ZERO : new BigDecimal(txtTotalBancos.getText());
            BigDecimal totalContable = txtTotalContable.getText().isEmpty() ?
                BigDecimal.ZERO : new BigDecimal(txtTotalContable.getText());

            if (cuadreSeleccionado == null) {
                // Crear nuevo cuadre
                Cuadre nuevoCuadre = cuadreController.crearCuadre(fecha, puntoVentaId,
                        usuarioActual != null ? usuarioActual.getId() : null);
                cuadreController.actualizarTotales(nuevoCuadre.getId(), totalTirilla, totalBancos, totalContable);
            } else {
                // Actualizar cuadre existente
                cuadreSeleccionado.setFecha(fecha);
                cuadreSeleccionado.setPuntoVentaId(puntoVentaId);
                cuadreSeleccionado.setTotalTirilla(totalTirilla);
                cuadreSeleccionado.setTotalBancos(totalBancos);
                cuadreSeleccionado.setTotalContable(totalContable);
                cuadreSeleccionado.setActualizadoPor(usuarioActual.getId());

                cuadreController.actualizarTotales(cuadreSeleccionado.getId(), totalTirilla, totalBancos, totalContable);
                tablaCuadres.refresh();
            }

            limpiarFormulario();
            mostrarMensaje("Cuadre guardado", "El cuadre ha sido guardado correctamente.");
            cargarCuadres();
        } catch (Exception e) {
            mostrarError("Error al guardar cuadre", e.getMessage());
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
     * Busca cuadres según el texto ingresado y filtros aplicados.
     */
    private void buscarCuadres() {
        aplicarFiltros();
    }
    
    /**
     * Aplica los filtros de búsqueda y estado.
     */
    private void aplicarFiltros() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        String estadoFiltro = cbFiltroEstado.getValue();
        
        cuadresListFiltrada.clear();
        
        for (Cuadre cuadre : cuadresList) {
            boolean coincideTexto = textoBusqueda.isEmpty() ||
                cuadre.getId().toString().contains(textoBusqueda) ||
                cuadre.getFecha().toString().contains(textoBusqueda) ||
                nombresPuntosVenta.getOrDefault(cuadre.getPuntoVentaId(), "")
                        .toLowerCase().contains(textoBusqueda);

            boolean coincideEstado = estadoFiltro.equals("Todos") ||
                cuadre.getEstado().toString().equals(estadoFiltro);

            if (coincideTexto && coincideEstado) {
                cuadresListFiltrada.add(cuadre);
            }
        }
        
        tablaCuadres.setItems(cuadresListFiltrada);
    }
    
    /**
     * Limpia la búsqueda y filtros, mostrando todos los cuadres.
     */
    private void limpiarBusqueda() {
        txtBuscar.clear();
        cbFiltroEstado.getSelectionModel().selectFirst(); // "Todos"
        tablaCuadres.setItems(cuadresList);
        tablaCuadres.refresh();
    }

    private void cargarPuntosDeVenta() {
        List<PuntoVenta> puntosVenta = puntoVentaController.obtenerTodos();
        nombresPuntosVenta.clear();
        puntosVenta.forEach(pv -> nombresPuntosVenta.put(pv.getId(), pv.getNombre()));

        ObservableList<String> opciones = FXCollections.observableArrayList();
        for (PuntoVenta pv : puntosVenta) {
            opciones.add(pv.getId() + " - " + pv.getNombre());
        }

        cbPuntoVenta.setItems(opciones);
        if (!opciones.isEmpty()) {
            cbPuntoVenta.getSelectionModel().selectFirst();
        }
    }
}
