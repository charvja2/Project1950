package cz.cvut.fel.cyber.dca.engine.gui;

import cz.cvut.fel.cyber.dca.engine.experiment.ExperimentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.ServerSocket;

/**
 * Created by Jan on 09.10.2016.
 */
public class ControlGui extends Application {

    private SimpleBooleanProperty upPressed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty downPressed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty leftPressed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty rightPressed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty wPressed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty dPressed = new SimpleBooleanProperty(false);



    @Override
    public void start(Stage primaryStage) {

        TextArea textArea = new TextArea("");
        textArea.setMinHeight(40.0);

        ServiceLogger.register(textArea);
        Label infoLabel = new Label("Enter object name:");
        TextField objectNameField = new TextField("");
        objectNameField.setFocusTraversable(false);

        Button btn = new Button("Get object");
        Button connectButton = new Button("Connect");
        Button stopButton = new Button("Stop simulation");

        connectButton.setOnAction(event -> {

        });
        btn.setOnAction(event -> {


        });
        stopButton.setOnAction(event -> {
            ServiceLogger.log("Stoping experiment.");
        });


        btn.setVisible(true);
        VBox centerPane = new VBox();
        centerPane.getChildren().addAll(infoLabel, objectNameField, btn, connectButton, stopButton);

        VBox centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane();
        AnchorPane root = new AnchorPane();
        root.getChildren().add(centerPane);
        root.getChildren().add(borderPane);
        root.getChildren().add(textArea);
        borderPane.setCenter(centerBox);

        AnchorPane.setBottomAnchor(textArea,0.0);
        AnchorPane.setLeftAnchor(textArea,0.0);
        AnchorPane.setRightAnchor(textArea,0.0);

        AnchorPane.setTopAnchor(centerPane,15.0);
        AnchorPane.setRightAnchor(centerPane, 30.0);

        AnchorPane.setBottomAnchor(centerPane, 15.0);

        AnchorPane.setTopAnchor(borderPane, 15.0);
        AnchorPane.setLeftAnchor(borderPane, 30.0);

        root.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP: {
                    Platform.runLater(() -> {
                    });

                }
                break;
                case DOWN: {
                    Platform.runLater(() -> {
                    });

                }
                break;
                case LEFT: {
                    Platform.runLater(() -> {
                    });
                }
                break;
                case RIGHT: {
                    Platform.runLater(() -> {
                    });
                }
                break;
                case W:{
                    Platform.runLater(() -> {

                    });
                } break;
                case D:{
                    Platform.runLater(() -> {

                    });
                } break;
            }
            event.consume();
        });


        Scene scene = new Scene(root, 400, 400);

        primaryStage.setTitle("V-REP control gui!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
