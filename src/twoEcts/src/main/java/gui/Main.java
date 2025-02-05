package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

/*

    // TODO - to powinien odpalać guzik start w GUI v
    private static boolean initManagers() {
        TimestampManager.getInstance().Activate();
        AudioCaptureManager.getInstance().Activate();
        ScreenCaptureManager.getInstance().Activate();

        return TimestampManager.getInstance().isActivated() && AudioCaptureManager.getInstance().isActive()
                && ScreenCaptureManager.getInstance().isActivated();

        // TODO - po tym wywołaniu > run note creator
    }

*/

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/AppStyles.css").toExternalForm());

        primaryStage.setTitle("Screen & Audio Recorder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
