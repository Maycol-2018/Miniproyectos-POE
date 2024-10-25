package com.example.miniproyecto2.model;

import com.example.miniproyecto2.controller.GameController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * This class implements the IChronometer Interface. This class contains the stopwatch methods, which shows how long
 * it is taking you to solve the sudoku
 * @author Maycol Andres Taquez Carlosama
 * @code 2375000
 * @author Santiago Valencia
 * @code 23
 */

public class Chronometer implements IChronometer{
    /**
     * Instance of the class Timeline
     * @serialField
     */
    private Timeline timeline;
    /**
     * Minute counter
     * @serialField
     */
    private int minutes;
    /**
     * Second counter
     * @serialField
     */
    private int seconds;
    /**
     * Instance of the class GameController
     * @serialField
     */
    private GameController controller;

    /**
     * A constructor of the "Chronometer" class that receives as a parameter an object of the "GameController" class
     * that is associated with the FXML file.
     * @param controller class object GameController
     */

    public Chronometer(GameController controller) {

        this.controller = controller;

        // Instance that Initializes the Timeline with 1 second intervals.
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            updateTime();
            updateLabel();
        }));

        // Set the Timeline to repeat indefinitely
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Method that updates seconds and minutes
     */
    @Override
    public void updateTime(){
        seconds ++;
        if(seconds == 60){
            seconds = 0;
            minutes ++;
        }
    }

    /**
     * Method that updates the Label with minutes and seconds
     */
    @Override
    public void updateLabel() {
        // Format that ensures that minutes and seconds are in two digits
        String timeString = String.format("%02d:%02d", minutes, seconds);
        controller.getLabelTime().setText(timeString);
    }

    /**
     * Method that start the Chronometer
     */
    public void start() {
        timeline.play();
    }

    /**
     * Method that stop the Chronometer
     */
    public void stop() {
        timeline.stop();
    }
    /**
     * Method that restart the Chronometer
     */
    public void restart(){
        minutes = 0;
        seconds = 0;
    }

}
