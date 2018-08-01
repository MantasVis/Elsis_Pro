/**
 * Elsis Pro bandomoji užduotis
 * Autorius: Mantas Visockis
 */

package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static Main.ControllerMain.getWriter;
import static Main.ControllerMain.spausdintiIFaila;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("/mainWindow.fxml"));
        primaryStage.setTitle("Elsis Pro bandomoji užduotis");
        primaryStage.setScene(new Scene(root, 358, 305));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop()
    {
        spausdintiIFaila("Programa buvo uždaryta.");
        getWriter().close();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
