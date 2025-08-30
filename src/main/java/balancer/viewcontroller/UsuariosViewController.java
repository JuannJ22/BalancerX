package balancer.viewcontroller;

import balancer.model.Usuario;
import balancer.security.AuthorizationService;
import balancer.model.Permiso;
import balancer.service.UsuarioService;
import balancer.util.Navigator;
import balancer.util.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UsuariosViewController {
    @FXML private TableView<Usuario> tabla;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPass;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cbRol;
    @FXML private Label lblInfo;
    private final UsuarioService service = new UsuarioService();
    private final ObservableList<Usuario> datos = FXCollections.observableArrayList();
    @FXML public void initialize(){
        if(!AuthorizationService.puede(Sesion.getUsuarioActual(), Permiso.GESTION_USUARIOS)){
            lblInfo.setText("Acceso restringido: solo superadministrador");
            return;
        }
        lblInfo.setText("Gestión de usuarios");
        colUsuario.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUsername()));
        colRol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRol()));
        cbRol.getItems().addAll("SUPERADMIN","ADMIN","USUARIO");
        datos.setAll(service.listar());
        tabla.setItems(datos);
    }
    @FXML public void agregar(){
        if(!AuthorizationService.puede(Sesion.getUsuarioActual(), Permiso.GESTION_USUARIOS)) return;
        service.agregar(txtUsuario.getText(), txtPass.getText(), txtNombre.getText(), cbRol.getValue());
        datos.setAll(service.listar());
        txtUsuario.clear(); txtPass.clear(); txtNombre.clear(); cbRol.getSelectionModel().clearSelection();
    }
    @FXML public void volver(){ Navigator.navigateTo("dashboard.fxml","Dashboard - Balancer"); }
}
