package com.example.miniproyecto2.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
/**
 * This class extends the Stage class. Load FXML files, and provides a singleton pattern implementation for the GameStage
 * It ensures only one instance of GameStage is created and reused.
 *
 * @author Maycol Andres Taquez Carlosama
 * @code 2375000
 * @author Santiago Valencia Aguiño
 * @code 2343334
 */

public class GameStage extends Stage {
    /**
     * Constructor: Initializes the stage by loading the FXML file, setting up the scene,
     * title, and window icon, and disabling window resizing.
     *
     * @throws IOException if there is an issue loading the FXML file.
     */
    public GameStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/miniproyecto2/game-view.fxml"));
        Parent root = loader.load();
        // Loads and sets the root node from the FXML file.
        Scene scene = new Scene(root);
        setScene(scene);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/miniproyecto2/StyleCss/game.css")).toExternalForm());

        // Add the window Icon
        getIcons().add(new Image(String.valueOf(getClass().getResource("/com/example/miniproyecto2/images/favicon.png"))));
        // Sets the window title
        setTitle("SUDOKU");
        // Disables window resizing.
        setResizable(false);
        // Display the stage
        show();
    }
    /**
     * Private static holder class that contains the singleton instance of GameStage.
     */
    private static class GameStageHolder{
        private static GameStage INSTANCE;
    }

    /**
     * Static method to get the singleton instance of GameStage.
     * If the instance is not yet created, it will instantiate a new GameStage.
     *
     * @return the singleton instance of GameStage.
     * @throws IOException if there is an issue loading resources.
     */
    public static GameStage getInstance() throws IOException{
        GameStageHolder.INSTANCE = GameStageHolder.INSTANCE != null?
                GameStageHolder.INSTANCE : new GameStage();
        return GameStageHolder.INSTANCE;
    }
    /**
     * Static method to delete the singleton instance of GameStage by closing its resources.
     */
    public static void deletedInstance(){
        GameStageHolder.INSTANCE.close();
        GameStageHolder.INSTANCE = null;
    }
}
