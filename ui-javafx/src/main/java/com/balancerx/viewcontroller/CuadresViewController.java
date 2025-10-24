package com.balancerx.viewcontroller;

import com.balancerx.controller.CuadreController;
import com.balancerx.model.entity.Cuadre;
import com.balancerx.model.entity.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    
    private CuadreController cuadreController;
    private ObservableList<Cuadre> cuadresList;
    private Usuario usuarioActual;
    private Cuadre cuadreSeleccionado;
    
    /**
     * Inicializa el controlador con el usuario autenticado.
     * @param usuario Usuario autenticado
     */
    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        this.cuadreController = new CuadreController(new com.balancerx.model.service.impl.CuadreServiceImpl());
        this.cuadresList = FXCollections.observableArrayList();
        
        // Configurar la tabla
        configurarTabla();
        
        // Configurar controles
        configurarControles();
        
        // Cargar datos
        cargarCuadres();
        
        // Configurar eventos
        configurarEventos();
    }
    
    /**
     * Configura las columnas de la tabla.
     */
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colPuntoVenta.setCellValueFactory(cellData -> {
            // En una implementación real, esto obtendría el nombre del punto de venta
            return javafx.beans.binding.Bindings.createStringBinding(() -> 
                "Punto de Venta " + cellData.getValue().getPuntoVentaId());
        });
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        
        tablaCuadres.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cuadreSeleccionado = newSelection;
                mostrarDetallesCuadre(cuadreSeleccionado);
            }
        });
    }
    
    /**
     * Configura los controles del formulario.
     */
    private void configurarControles() {
        dpFecha.setValue(LocalDate.now());
        
        // Cargar puntos de venta en el combo
        ObservableList<String> puntosVenta = FXCollections.observableArrayList(
            "Punto de Venta 1", "Punto de Venta 2", "Punto de Venta 3"
        );
        cbPuntoVenta.setItems(puntosVenta);
        cbPuntoVenta.getSelectionModel().selectFirst();
    }
    
    /**
     * Carga los cuadres en la tabla.
     */
    private void cargarCuadres() {
        try {
            cuadresList.clear();
            // En una implementación real, esto cargaría desde el controlador
            // cuadresList.addAll(cuadreController.obtenerTodos());
            
            // Por ahora, agregamos datos de ejemplo
            Cuadre cuadre1 = new Cuadre();
            cuadre1.setId(1L);
            cuadre1.setFecha(LocalDate.now());
            cuadre1.setPuntoVentaId(1L);
            cuadre1.setEstado(Cuadre.EstadoCuadre.PENDIENTE);
            cuadre1.setTotalTirilla(new BigDecimal("1000.00"));
            cuadre1.setTotalBancos(new BigDecimal("950.00"));
            cuadre1.setTotalContable(new BigDecimal("975.00"));
            
            Cuadre cuadre2 = new Cuadre();
            cuadre2.setId(2L);
            cuadre2.setFecha(LocalDate.now().minusDays(1));
            cuadre2.setPuntoVentaId(2L);
            cuadre2.setEstado(Cuadre.EstadoCuadre.APROBADO);
            cuadre2.setTotalTirilla(new BigDecimal("2000.00"));
            cuadre2.setTotalBancos(new BigDecimal("2000.00"));
            cuadre2.setTotalContable(new BigDecimal("2000.00"));
            
            cuadresList.add(cuadre1);
            cuadresList.add(cuadre2);
            
            tablaCuadres.setItems(cuadresList);
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
    }
    
    /**
     * Muestra los detalles del cuadre seleccionado.
     * @param cuadre Cuadre seleccionado
     */
    private void mostrarDetallesCuadre(Cuadre cuadre) {
        dpFecha.setValue(cuadre.getFecha());
        cbPuntoVenta.getSelectionModel().select("Punto de Venta " + cuadre.getPuntoVentaId());
        
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
        cbPuntoVenta.getSelectionModel().selectFirst();
        txtTotalTirilla.clear();
        txtTotalBancos.clear();
        txtTotalContable.clear();
    }
    
    /**
     * Guarda el cuadre actual.
     */
    private void guardarCuadre() {
        try {
            LocalDate fecha = dpFecha.getValue();
            String puntoVentaStr = cbPuntoVenta.getValue();
            Long puntoVentaId = Long.parseLong(puntoVentaStr.replace("Punto de Venta ", ""));
            
            BigDecimal totalTirilla = txtTotalTirilla.getText().isEmpty() ? 
                BigDecimal.ZERO : new BigDecimal(txtTotalTirilla.getText());
            BigDecimal totalBancos = txtTotalBancos.getText().isEmpty() ? 
                BigDecimal.ZERO : new BigDecimal(txtTotalBancos.getText());
            BigDecimal totalContable = txtTotalContable.getText().isEmpty() ? 
                BigDecimal.ZERO : new BigDecimal(txtTotalContable.getText());
            
            if (cuadreSeleccionado == null) {
                // Crear nuevo cuadre
                Cuadre nuevoCuadre = new Cuadre();
                nuevoCuadre.setFecha(fecha);
                nuevoCuadre.setPuntoVentaId(puntoVentaId);
                nuevoCuadre.setEstado(Cuadre.EstadoCuadre.PENDIENTE);
                nuevoCuadre.setTotalTirilla(totalTirilla);
                nuevoCuadre.setTotalBancos(totalBancos);
                nuevoCuadre.setTotalContable(totalContable);
                nuevoCuadre.setCreadoPor(usuarioActual.getId());
                
                // En una implementación real, esto guardaría usando el controlador
                // cuadreController.crearCuadre(fecha, puntoVentaId, usuarioActual.getId());
                // cuadreController.actualizarTotales(nuevoCuadre.getId(), totalTirilla, totalBancos, totalContable);
                
                // Por ahora, simulamos la asignación de un ID
                nuevoCuadre.setId((long) (cuadresList.size() + 1));
                cuadresList.add(nuevoCuadre);
            } else {
                // Actualizar cuadre existente
                cuadreSeleccionado.setFecha(fecha);
                cuadreSeleccionado.setPuntoVentaId(puntoVentaId);
                cuadreSeleccionado.setTotalTirilla(totalTirilla);
                cuadreSeleccionado.setTotalBancos(totalBancos);
                cuadreSeleccionado.setTotalContable(totalContable);
                cuadreSeleccionado.setActualizadoPor(usuarioActual.getId());
                
                // En una implementación real, esto actualizaría usando el controlador
                // cuadreController.actualizarTotales(cuadreSeleccionado.getId(), totalTirilla, totalBancos, totalContable);
                
                // Actualizar la tabla
                tablaCuadres.refresh();
            }
            
            limpiarFormulario();
            mostrarMensaje("Cuadre guardado", "El cuadre ha sido guardado correctamente.");
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
}