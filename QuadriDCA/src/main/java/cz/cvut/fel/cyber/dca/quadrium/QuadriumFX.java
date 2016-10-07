/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.cyber.dca.quadrium;

import coppelia.*;
import javafx.application.Application;
import static javafx.application.Application.launch;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


/**
 *
 * @author Jan
 */
public class QuadriumFX extends Application{

    private VREPSession session;
    private boolean start = false;
    private SimpleBooleanProperty upPressed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty downPressed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty leftPressed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty rightPressed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty wPressed = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty dPressed = new SimpleBooleanProperty(false);

    private double[] positionIncrRation = new double[3];
    private double[] positionIncr = new double[3];

    @Override
    public void start(Stage primaryStage) {

        for(int i =0 ; i < positionIncr.length;i++){
            positionIncr[i] = 0.0;
            positionIncrRation[i] = 0.01;
        }

        TextArea textArea = new TextArea("");
        textArea.setMinHeight(40.0);
        ServiceLogger.register(textArea);
        Label infoLabel = new Label("Enter object name:");
        TextField objectNameField = new TextField("");
        objectNameField.setFocusTraversable(false);
        Button btn = new Button("Get object");
        Button connectButton = new Button("Connect");
        Button startStopButton = new Button("Start");
        connectButton.setOnAction(event -> {
            if(connectButton.getText().equals("Connect")){
                session = new VREPSession("127.0.0.1");
                if(session.connect()){
                    infoLabel.setText("Connected!");
                    ServiceLogger.log("Connected!");
                    connectButton.setText("Disconnect");
                }
                else {
                    infoLabel.setText("Failed to connect to remoteApi!");}
            }else{
                session.disconnect();
                infoLabel.setText("Disconnected!");
                ServiceLogger.log("Disconnected!");
                connectButton.setText("Connect");
            }
        });
        btn.setOnAction(event -> {
            String objectName = objectNameField.getText();
            IntW handle = new IntW(1);
            int errorCode = session.getHandle(objectName,handle);
            ServiceLogger.log(">>>>> ErrorCode: " + Integer.toString(errorCode) + " >>>>> Object handle: " + Integer.toString(handle.getValue()));

        });
        startStopButton.setOnAction(event -> {
            if(startStopButton.getText().equals("Start")){
                start = true;
                startStopButton.setText("Stop");
                ServiceLogger.log(">>>>> Vector3 control started!");
                Task<Void> controlTask = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        while(start == true){
                                long startTime = System.currentTimeMillis();
                                FloatWA heliJointPosition = new FloatWA(3);
                                int ret = session.getVrep().simxGetObjectPosition(session.getClientID(), session.getHandles().get(objectNameField.getText()).getValue(), -1, heliJointPosition, remoteApi.simx_opmode_oneshot_wait);
                                if (ret != remoteApi.simx_return_ok) {
                                    System.out.println("Failed to receive position for joint. Error code:  " + ret);
                                }else System.out.printf("[%f %f %f]\n", heliJointPosition.getArray()[0], heliJointPosition.getArray()[1], heliJointPosition.getArray()[2]);

                                long duration = System.currentTimeMillis() - startTime;
                                for(int i = 0; i<3;i++){
                                    heliJointPosition.getArray()[i]+=positionIncr[i]*(startTime*1000);
                                }
                                ServiceLogger.log(">>>>> [" + Float.toString( heliJointPosition.getArray()[0])+ " , " + Float.toString( heliJointPosition.getArray()[1])+ " , " + Float.toString( heliJointPosition.getArray()[2])+ "]");
                                ServiceLogger.log(">>>>> [" + Double.toString( positionIncr[0])+ " , " + Double.toString( positionIncr[1])+ " , " + Double.toString( positionIncr[2])+ "]");


                                ret = session.getVrep().simxSetObjectPosition(session.getClientID(), session.getHandles().get(objectNameField.getText()).getValue(), -1, heliJointPosition, remoteApi.simx_opmode_oneshot_wait);
                                if (ret != remoteApi.simx_return_ok) {
                                    ServiceLogger.log(">>>>> Failed to set object position!");
                                }
                            Thread.sleep(100);
                        }
                        return null;
                    }
                };
                new Thread(controlTask).start();
            }else{
                start = false;
                startStopButton.setText("Start");
                ServiceLogger.log(">>>>> Vector3 control stopped!");
            }
        });
        Rectangle upperRec = new Rectangle(100,10);
        Rectangle leftRec = new Rectangle(10,100);
        Rectangle rightRec = new Rectangle(10,100);
        Rectangle bottomRec = new Rectangle(100,10);
        Rectangle wRec = new Rectangle(50,10);
        Rectangle constRec = new Rectangle(50,10);
        Rectangle dRec = new Rectangle(50,10);
        upperRec.setFill(Color.GAINSBORO);
        leftRec.setFill(Color.GAINSBORO);
        rightRec.setFill(Color.GAINSBORO);
        bottomRec.setFill(Color.GAINSBORO);
        wRec.setFill(Color.GAINSBORO);
        constRec.setFill(Color.GREEN);
        dRec.setFill(Color.GAINSBORO);

        btn.setVisible(true);
        VBox centerPane = new VBox();
        centerPane.getChildren().addAll(infoLabel, objectNameField, btn, connectButton, startStopButton);

        VBox centerBox = new VBox();
        centerBox.getChildren().addAll(wRec, constRec, dRec);
        centerBox.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane();
        AnchorPane root = new AnchorPane();
        root.getChildren().add(centerPane);
        root.getChildren().add(borderPane);
        root.getChildren().add(textArea);
        borderPane.setBottom(bottomRec);
        borderPane.setTop(upperRec);
        borderPane.setLeft(leftRec);
        borderPane.setRight(rightRec);
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
                        upperRec.setFill(Color.GREEN);
                    });
                    upPressed.set(true);
                    positionIncr[0]+=positionIncrRation[0];
                }
                break;
                case DOWN: {
                    Platform.runLater(() -> {
                        bottomRec.setFill(Color.GREEN);
                    });
                    downPressed.set(true);
                    positionIncr[0]-=positionIncrRation[0];
                }
                break;
                case LEFT: {
                    Platform.runLater(() -> {
                        leftRec.setFill(Color.GREEN);
                    });
                    leftPressed.set(true);
                    positionIncr[1]+=positionIncrRation[1];
                }
                break;
                case RIGHT: {
                    Platform.runLater(() -> {
                        rightRec.setFill(Color.GREEN);
                    });
                    rightPressed.set(true);
                    positionIncr[1]-=positionIncrRation[1];
                }
                break;
                case W:{
                    Platform.runLater(() -> {
                        wRec.setFill(Color.GREEN);
                        constRec.setFill(Color.GAINSBORO);
                    });
                    wPressed.set(true);
                    positionIncr[2]+=positionIncrRation[2];
                } break;
                case D:{
                    Platform.runLater(() -> {
                        dRec.setFill(Color.GREEN);
                        constRec.setFill(Color.GAINSBORO);
                    });
                    dPressed.set(true);
                    positionIncr[2]-=positionIncrRation[2];
                } break;
            }
            event.consume();
        });
        root.setOnKeyReleased(event -> {
            switch(event.getCode()){
                case UP:{
                    Platform.runLater(() -> {
                        upPressed.set(false);
                        upperRec.setFill(Color.GAINSBORO);
                    });
                    positionIncr[0]-=positionIncrRation[0];
                } break;
                case DOWN:{
                    Platform.runLater(() -> {
                        downPressed.set(false);
                        bottomRec.setFill(Color.GAINSBORO);
                    });
                    positionIncr[0]+=positionIncrRation[0];
                } break;
                case LEFT:{
                    Platform.runLater(() -> {
                        leftPressed.set(false);
                        leftRec.setFill(Color.GAINSBORO);
                    });

                    positionIncr[1]-=positionIncrRation[1];
                } break;
                case RIGHT:{
                    Platform.runLater(() -> {
                        rightPressed.set(false);
                        rightRec.setFill(Color.GAINSBORO);
                    });

                    positionIncr[1]+=positionIncrRation[1];
                } break;
                case W:{
                    Platform.runLater(() -> {
                        wPressed.set(false);
                        wRec.setFill(Color.GAINSBORO);
                        constRec.setFill(Color.GREEN);
                    });
                    positionIncr[2]-=positionIncrRation[2];
                } break;
                case D:{
                    Platform.runLater(() -> {
                        dPressed.set(false);
                        rightRec.setFill(Color.GAINSBORO);
                        constRec.setFill(Color.GREEN);
                    });
                    positionIncr[2]+=positionIncrRation[2];
                } break;
            }
            event.consume();
        });
        
        Scene scene = new Scene(root, 400, 400);
        
        primaryStage.setTitle("QuadriumFX!");
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
