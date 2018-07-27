package de.bcersows.photoimporter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(final Stage primaryStage) throws Exception {
        final Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
        primaryStage.setTitle("FXML-Beispiel");
        final Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style/application.css").toExternalForm());

        Font.loadFont(getClass().getResource("/fonts/Montserrat-Regular.ttf").toExternalForm(), 36);
        Font.loadFont(getClass().getResource("/fonts/Montserrat-Medium.ttf").toExternalForm(), 36);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }

}
