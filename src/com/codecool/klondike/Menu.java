package com.codecool.klondike;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.awt.*;

public class Menu {
    private static Stage menu = new Stage();

    public static void displayMenu(Game game){

        menu = new Stage();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        menu.initModality(Modality.APPLICATION_MODAL);
        menu.setTitle("Menu");
        menu.setMinWidth(500);
        menu.setMinHeight(400);
        menu.centerOnScreen();
        menu.setX(width / 2 - 250);
        menu.setY(height / 2 - 250);

        VBox layout = new VBox(30);
        Button restartButton = new Button("Restart");
        Button switchButton = new Button("Switch");
        Button closeButton = new Button("Exit");
        Button resumeButton = new Button("Resume");

        game.buttonStyle(resumeButton);
        game.buttonStyle(restartButton);
        game.buttonStyle(closeButton);
        game.buttonStyle(switchButton);
        closeButton.setMinWidth(175);
        restartButton.setMinWidth(175);
        switchButton.setMinWidth(175);
        resumeButton.setMinWidth(175);
        closeButton.setMinHeight(50);
        restartButton.setMinHeight(50);
        switchButton.setMinHeight(50);
        resumeButton.setMinHeight(50);


        layout.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(resumeButton,restartButton,switchButton,closeButton);

        layout.setBackground(new Background(new BackgroundImage(new Image("/table/green.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        game.addButtonRestartHandler(restartButton);
        game.addSwitchButtonHandler(switchButton,game.getSwitchBackground());

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.exit();
            }
        });

        resumeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                close();
            }
        });

        Scene scene = new Scene(layout);
        menu.setScene(scene);
        menu.show();
    }
    public static void close() {
        menu.close();
    }
}

