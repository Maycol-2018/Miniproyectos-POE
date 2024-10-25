package com.example.miniproyecto2;
import com.example.miniproyecto2.view.GameStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main class of the JavaFX application.
 * This class extends the JavaFX Application class and serves as the entry point for the application.
 *
 * @author Maycol Andres Taquez Carlosama
 * @code 2375000
 * @author Santiago Valencia Aguiño
 * @code 2343334
 */
public class Main extends Application {
    /**
     * Main method that starts the application.
     * Calls the launch() method to initiate the JavaFX application lifecycle.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
    /**
     * Overridden start() method that is executed once the application is launched.
     *
     * @param primaryStage The main stage of the JavaFX application.
     * @throws IOException If an error occurs while loading the user interface.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        GameStage.getInstance();
    }
}