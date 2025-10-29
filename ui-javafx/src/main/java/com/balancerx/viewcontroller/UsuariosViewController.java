package com.balancerx.viewcontroller;

import com.balancerx.AppContext;
import com.balancerx.controller.UsuarioController;
import com.balancerx.model.entity.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controlador de vista para la gestión de usuarios.
 */
public class UsuariosViewController {

    @FXML
    private TableView<Usuario> tablaUsuarios;
    
    @FXML
    private TableColumn<Usuario, Long> colId;
    
    @FXML
    private TableColumn<Usuario, String> colNombre;
    
    @FXML
    private TableColumn<Usuario, String> colEmail;
    
    @FXML
    private TableColumn<Usuario, String> colRol;
    
    @FXML
    private TableColumn<Usuario, Boolean> colActivo;
    
    @FXML
    private TextField txtNombre;
    
    @FXML
    private TextField txtEmail;
    
    @FXML
    private PasswordField txtPassword;
    
    @FXML
    private ComboBox<String> cbRol;
    
    @FXML
    private CheckBox chkActivo;
    
    @FXML
    private Button btnGuardar;
    
    @FXML
    private Button btnNuevo;
    
    @FXML
    private TextField txtBuscar;
    
    @FXML
    private Button btnBuscar;
    
    @FXML
    private Button btnLimpiarBusqueda;

    @FXML
    private Button btnEliminar;

    private UsuarioController usuarioController;
    private ObservableList<Usuario> usuariosList;
    private ObservableList<Usuario> usuariosListFiltrada;
    private Usuario usuarioActual;
    private Usuario usuarioSeleccionado;
    private AppContext appContext;
    
    /**
     * Inicializa el controlador con el usuario autenticado.
     * @param usuario Usuario autenticado
     */
    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        if (usuarioController == null) {
            setAppContext(AppContext.getInstance());
        }
        this.usuariosList = FXCollections.observableArrayList();
        this.usuariosListFiltrada = FXCollections.observableArrayList();

        // Configurar la tabla
        configurarTabla();
        
        // Configurar controles
        configurarControles();
        
        // Cargar datos
        cargarUsuarios();
        
        // Configurar eventos
        configurarEventos();
    }

    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        this.usuarioController = appContext.getUsuarioController();
    }
    
    /**
     * Configura las columnas de la tabla.
     */
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                usuarioSeleccionado = newSelection;
                mostrarDetallesUsuario(usuarioSeleccionado);
                btnEliminar.setDisable(false);
            } else {
                usuarioSeleccionado = null;
                btnEliminar.setDisable(true);
            }
        });
    }
    
    /**
     * Configura los controles del formulario.
     */
    private void configurarControles() {
        // Cargar roles en el combo
        List<String> roles = Arrays.asList("ADMIN", "ELABORADOR", "BANCO", "ASIGNADOR", "AUDITOR");
        cbRol.setItems(FXCollections.observableArrayList(roles));
        cbRol.getSelectionModel().selectFirst();

        chkActivo.setSelected(true);

        btnEliminar.setDisable(true);
    }
    
    /**
     * Carga los usuarios en la tabla.
     */
    private void cargarUsuarios() {
        try {
            usuariosList.setAll(usuarioController.obtenerTodosLosUsuarios());
            tablaUsuarios.setItems(usuariosList);
            tablaUsuarios.getSelectionModel().clearSelection();
            actualizarTextoBotonEstado();
        } catch (Exception e) {
            mostrarError("Error al cargar usuarios", e.getMessage());
        }
    }
    
    /**
     * Configura los eventos de los botones.
     */
    private void configurarEventos() {
        btnNuevo.setOnAction(event -> limpiarFormulario());
        btnGuardar.setOnAction(event -> guardarUsuario());
        btnBuscar.setOnAction(event -> buscarUsuarios());
        btnLimpiarBusqueda.setOnAction(event -> limpiarBusqueda());
        btnEliminar.setOnAction(event -> cambiarEstadoUsuario());

        // Búsqueda en tiempo real
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                limpiarBusqueda();
            }
        });
    }
    
    /**
     * Muestra los detalles del usuario seleccionado.
     * @param usuario Usuario seleccionado
     */
    private void mostrarDetallesUsuario(Usuario usuario) {
        txtNombre.setText(usuario.getNombre());
        txtEmail.setText(usuario.getEmail());
        txtPassword.clear(); // No mostramos la contraseña por seguridad
        cbRol.setValue(usuario.getRol());
        chkActivo.setSelected(usuario.isActivo());
        actualizarTextoBotonEstado();
    }
    
    /**
     * Limpia el formulario para un nuevo usuario.
     */
    private void limpiarFormulario() {
        usuarioSeleccionado = null;
        txtNombre.clear();
        txtEmail.clear();
        txtPassword.clear();
        cbRol.getSelectionModel().selectFirst();
        chkActivo.setSelected(true);
        txtNombre.requestFocus();
        btnEliminar.setDisable(true);
        actualizarTextoBotonEstado();
    }
    
    /**
     * Guarda el usuario actual.
     */
    private void guardarUsuario() {
        try {
            String nombre = txtNombre.getText().trim();
            String email = txtEmail.getText().trim();
            String password = txtPassword.getText();
            String rol = cbRol.getValue();
            boolean activo = chkActivo.isSelected();

            // Validaciones
            if (nombre.isEmpty() || email.isEmpty()) {
                mostrarError("Error de validación", "El nombre y el email son obligatorios");
                return;
            }
            
            if (usuarioSeleccionado == null && password.isEmpty()) {
                mostrarError("Error de validación", "La contraseña es obligatoria para nuevos usuarios");
                return;
            }

            if (usuarioSeleccionado == null) {
                // Crear nuevo usuario
                Usuario nuevoUsuario = usuarioController.registrarUsuario(nombre, email, password, rol, activo);
                usuariosList.add(nuevoUsuario);
            } else {
                // Actualizar usuario existente
                Optional<Usuario> actualizado = usuarioController.actualizarUsuario(
                        usuarioSeleccionado.getId(), nombre, email, rol, activo, password.isEmpty() ? null : password);

                if (actualizado.isPresent()) {
                    usuarioSeleccionado = actualizado.get();
                    tablaUsuarios.refresh();
                    actualizarTextoBotonEstado();
                } else {
                    mostrarError("Usuario no encontrado", "No se pudo actualizar el usuario seleccionado");
                }
            }

            limpiarFormulario();
            mostrarMensaje("Usuario guardado", "El usuario ha sido guardado correctamente.");
            cargarUsuarios();
        } catch (Exception e) {
            mostrarError("Error al guardar usuario", e.getMessage());
        }
    }

    private void cambiarEstadoUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarError("Sin selección", "Seleccione un usuario para cambiar su estado");
            return;
        }

        boolean nuevoEstado = !usuarioSeleccionado.isActivo();
        Optional<Usuario> actualizado = usuarioController.cambiarEstadoActivacion(usuarioSeleccionado.getId(), nuevoEstado);

        if (actualizado.isPresent()) {
            usuarioSeleccionado = actualizado.get();
            tablaUsuarios.refresh();
            actualizarTextoBotonEstado();
        } else {
            mostrarError("Error", "No se pudo actualizar el estado del usuario");
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
     * Busca usuarios según el texto ingresado.
     */
    private void buscarUsuarios() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        
        if (textoBusqueda.isEmpty()) {
            limpiarBusqueda();
            return;
        }
        
        usuariosListFiltrada.clear();
        for (Usuario usuario : usuariosList) {
            if (usuario.getNombre().toLowerCase().contains(textoBusqueda) ||
                usuario.getEmail().toLowerCase().contains(textoBusqueda) ||
                usuario.getRol().toLowerCase().contains(textoBusqueda)) {
                usuariosListFiltrada.add(usuario);
            }
        }

        tablaUsuarios.setItems(usuariosListFiltrada);
    }
    
    /**
     * Limpia la búsqueda y muestra todos los usuarios.
     */
    private void limpiarBusqueda() {
        txtBuscar.clear();
        tablaUsuarios.setItems(usuariosList);
        tablaUsuarios.refresh();
        usuariosListFiltrada.clear();
        actualizarTextoBotonEstado();
    }

    private void actualizarTextoBotonEstado() {
        if (btnEliminar == null) {
            return;
        }

        if (usuarioSeleccionado == null) {
            btnEliminar.setText("Desactivar");
            return;
        }

        btnEliminar.setText(usuarioSeleccionado.isActivo() ? "Desactivar" : "Activar");
    }
}
