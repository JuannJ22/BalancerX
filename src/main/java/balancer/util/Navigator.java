package balancer.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public final class Navigator {
    private static Stage stage;
    private Navigator(){}
    public static void setStage(Stage s){ stage = s; }
    public static <T> T navigateTo(String fxml, String title){
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource("/balancer/view/" + fxml));
            Parent root = loader.load();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 900, 650));
            stage.show();
            return loader.getController();
        } catch (IOException e) { throw new RuntimeException("No se pudo cargar: " + fxml, e); }
    }
}
