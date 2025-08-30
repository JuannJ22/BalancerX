package balancer.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public final class DialogosUtil {
    private DialogosUtil(){}
    public static void info(String titulo, String msg){
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle(titulo); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
        });
    }
    public static void error(String titulo, String msg){
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle(titulo); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
        });
    }
}
