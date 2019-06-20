package com.codecool.klondike;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.geometry.*;

import java.awt.*;

public class Congratulation {

    private static Stage window = new Stage();

    public static void display(String title, String message, Game game) {
        window = new Stage();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(500);
        window.centerOnScreen();
        window.setX(width / 2 - 250);
        window.setY(height / 2 - 250);


        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("Exit");
        Button restartButton = new Button("Restart");

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.exit();
            }
        });
        game.addButtonRestartHandler(restartButton);

        VBox layout = new VBox(30);
        closeButton.setAlignment(Pos.CENTER);
        restartButton.setAlignment(Pos.CENTER);

        layout.getChildren().add(label);
        layout.getChildren().add(restartButton);
        layout.getChildren().add(closeButton);
        layout.setAlignment(Pos.CENTER);

        layout.setBackground(new Background(new BackgroundImage(new Image("/table/green.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();

    }

    public static void close() {
        window.close();
    }
}
