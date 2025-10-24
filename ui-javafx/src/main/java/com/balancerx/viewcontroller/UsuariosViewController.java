package com.balancerx.viewcontroller;

import com.balancerx.controller.UsuarioController;
import com.balancerx.model.entity.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
    
    private UsuarioController usuarioController;
    private ObservableList<Usuario> usuariosList;
    private Usuario usuarioActual;
    private Usuario usuarioSeleccionado;
    
    /**
     * Inicializa el controlador con el usuario autenticado.
     * @param usuario Usuario autenticado
     */
    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        this.usuarioController = new UsuarioController(new com.balancerx.model.service.impl.UsuarioServiceImpl());
        this.usuariosList = FXCollections.observableArrayList();
        
        // Configurar la tabla
        configurarTabla();
        
        // Configurar controles
        configurarControles();
        
        // Cargar datos
        cargarUsuarios();
        
        // Configurar eventos
        configurarEventos();
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
    }
    
    /**
     * Carga los usuarios en la tabla.
     */
    private void cargarUsuarios() {
        try {
            usuariosList.clear();
            // En una implementación real, esto cargaría desde el controlador
            // usuariosList.addAll(usuarioController.obtenerTodos());
            
            // Por ahora, agregamos datos de ejemplo
            Usuario admin = new Usuario(1L, "Administrador", "admin@balancerx.com", "ADMIN", "hash123", true, LocalDateTime.now());
            Usuario elaborador = new Usuario(2L, "Elaborador", "elaborador@balancerx.com", "ELABORADOR", "hash456", true, LocalDateTime.now());
            Usuario auditor = new Usuario(3L, "Auditor", "auditor@balancerx.com", "AUDITOR", "hash789", true, LocalDateTime.now());
            
            usuariosList.addAll(admin, elaborador, auditor);
            tablaUsuarios.setItems(usuariosList);
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
                Usuario nuevoUsuario = new Usuario(null, nombre, email, rol, "hash_" + password, activo, LocalDateTime.now());
                
                // En una implementación real, esto guardaría usando el controlador
                // usuarioController.registrar(nombre, email, password, rol);
                
                // Por ahora, simulamos la asignación de un ID
                nuevoUsuario.setId((long) (usuariosList.size() + 1));
                usuariosList.add(nuevoUsuario);
            } else {
                // Actualizar usuario existente
                usuarioSeleccionado.setNombre(nombre);
                usuarioSeleccionado.setEmail(email);
                usuarioSeleccionado.setRol(rol);
                usuarioSeleccionado.setActivo(activo);
                
                if (!password.isEmpty()) {
                    usuarioSeleccionado.setHashPassword("hash_" + password);
                }
                
                // En una implementación real, esto actualizaría usando el controlador
                // usuarioController.actualizar(usuarioSeleccionado.getId(), nombre, email, rol);
                // if (!password.isEmpty()) {
                //     usuarioController.cambiarPassword(usuarioSeleccionado.getId(), password);
                // }
                
                // Actualizar la tabla
                tablaUsuarios.refresh();
            }
            
            limpiarFormulario();
            mostrarMensaje("Usuario guardado", "El usuario ha sido guardado correctamente.");
        } catch (Exception e) {
            mostrarError("Error al guardar usuario", e.getMessage());
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