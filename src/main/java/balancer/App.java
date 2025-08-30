package balancer;

import balancer.util.ArchivoUtils;
import balancer.util.KeystoreManager;
import balancer.util.Navigator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ArchivoUtils.inicializar();
        KeystoreManager.inicializarSiNoExiste();
        Navigator.setStage(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/balancer/view/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 650);
        stage.setTitle("Balancer");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
