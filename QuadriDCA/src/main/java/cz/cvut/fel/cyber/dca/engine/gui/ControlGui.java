package cz.cvut.fel.cyber.dca.engine.gui;

import cz.cvut.fel.cyber.dca.engine.core.Swarm;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 09.10.2016.
 */
public class ControlGui extends Application {

    public static VBox centerPane;
    public static VBox labelPane;
    public static HBox buttonPane;

    public static Label simTimeLabel;

    public static Button connectButton;
    public static Button flockingBtn;
    public static Button boundaryDetBtn;
    public static Button boundaryTenBtn;
    public static Button leaderFollowBtn;
    public static Button thicknessBtn;
    public static Button densityBtn;
    public static Button heightControlBtn;
    public static Button followPathBtn;
    public static Button dimensionBtn;
    public static Button recordBtn;
    public static Button stopButton;


    public static void setBackCol(Button btn, boolean state){
        if(state)btn.setBackground(new Background(new BackgroundFill(Color.DARKGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        else btn.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public static List<Label> getLogLabelList(){
        List<Label> labelList = new ArrayList<>();
        ServiceLogger.registerLabelList(labelList);
        Swarm.getMembers().stream().forEach(member -> labelList.add(member.getId(), new Label(Integer.toString(member.getId()))));
        return labelList;
    }

    public static void refreshLabels(){
        Platform.runLater(() -> {
            labelPane.getChildren().clear();
            labelPane.getChildren().add(simTimeLabel);
            labelPane.getChildren().addAll(getLogLabelList());
        });
    }

    @Override
    public void start(Stage primaryStage) {
        TextArea textArea = new TextArea("");
        textArea.setMinHeight(800.0);
        textArea.setEditable(false);

        ServiceLogger.register(textArea);
        TextField inputField = new TextField("load; run");
        inputField.setFocusTraversable(false);

        List<Label> labelList = getLogLabelList();

        simTimeLabel = new Label("");
        ServiceLogger.registerSimTimeLabel(simTimeLabel);

        flockingBtn = new Button("Flocking");
        boundaryDetBtn = new Button("Boundary det");
        boundaryTenBtn = new Button("Boundary ten");
        leaderFollowBtn = new Button("Leader");
        thicknessBtn = new Button("Thickness");
        densityBtn = new Button("Density");
        heightControlBtn = new Button("Height");
        followPathBtn = new Button("Follow path");
        dimensionBtn = new Button("Dimension 2D/3D");
        recordBtn = new Button("Data log");
        connectButton = new Button("Connect");
        stopButton = new Button("Stop");

        setBackCol(flockingBtn, Experiment.FLOCKING_ALGORITHM_ACTIVATED);
        setBackCol(boundaryDetBtn, Experiment.BOUNDARY_DETECTION_ALGORITHM_ACTIVATED);
        setBackCol(boundaryTenBtn, Experiment.BOUNDARY_TENSION_ALGORITHM_ACTIVATED);
        setBackCol(leaderFollowBtn, Experiment.LEADER_FOLLOW_ALGORITHM_ACTIVATED);
        setBackCol(thicknessBtn, Experiment.THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED);
        setBackCol(densityBtn, Experiment.DENSITY_ALGORITHM_ACTIVATED);
        setBackCol(heightControlBtn, Experiment.HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED);
        setBackCol(recordBtn, Experiment.FLIGHT_RECORDING);
        setBackCol(followPathBtn, Experiment.LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED);

        dimensionBtn.setText("Dimension " + Integer.toString(Experiment.DIMENSION)+"D");


        flockingBtn.setOnAction(event1 -> {
            Experiment.FLOCKING_ALGORITHM_ACTIVATED = !Experiment.FLOCKING_ALGORITHM_ACTIVATED;
            setBackCol(flockingBtn, Experiment.FLOCKING_ALGORITHM_ACTIVATED);
        });

        boundaryDetBtn.setOnAction(event1 -> {
            Experiment.BOUNDARY_DETECTION_ALGORITHM_ACTIVATED = !Experiment.BOUNDARY_DETECTION_ALGORITHM_ACTIVATED;
            setBackCol(boundaryDetBtn, Experiment.BOUNDARY_DETECTION_ALGORITHM_ACTIVATED);
        });

        boundaryTenBtn.setOnAction(event1 -> {
            Experiment.BOUNDARY_TENSION_ALGORITHM_ACTIVATED = !Experiment.BOUNDARY_TENSION_ALGORITHM_ACTIVATED;
            setBackCol(boundaryTenBtn, Experiment.BOUNDARY_TENSION_ALGORITHM_ACTIVATED);
        });

        leaderFollowBtn.setOnAction(event1 -> {
            Experiment.LEADER_FOLLOW_ALGORITHM_ACTIVATED = !Experiment.LEADER_FOLLOW_ALGORITHM_ACTIVATED;
            setBackCol(leaderFollowBtn, Experiment.LEADER_FOLLOW_ALGORITHM_ACTIVATED);
        });

        thicknessBtn.setOnAction(event1 -> {
            Experiment.THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED = !Experiment.THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED;
            setBackCol(thicknessBtn, Experiment.THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED);
        });

        densityBtn.setOnAction(event1 -> {
            Experiment.DENSITY_ALGORITHM_ACTIVATED = !Experiment.DENSITY_ALGORITHM_ACTIVATED;
            setBackCol(densityBtn, Experiment.DENSITY_ALGORITHM_ACTIVATED);
        });

        heightControlBtn.setOnAction(event1 -> {
            Experiment.HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED = !Experiment.HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED;
            setBackCol(heightControlBtn, Experiment.HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED);
        });

        followPathBtn.setOnAction(event1 -> {
            Experiment.LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED = !Experiment.LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED;
            setBackCol(followPathBtn, Experiment.LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED);
        });

        dimensionBtn.setOnAction(event1 -> {
            int originalValue = Experiment.DIMENSION;
            if(Experiment.DIMENSION == 2) Experiment.DIMENSION = 3;
            else Experiment.DIMENSION = 2;
            dimensionBtn.setText("Dimension " + Integer.toString(originalValue)+"D");
            ServiceLogger.log("Dimension switched to: " + Integer.toString(Experiment.DIMENSION)+"D");
        });

        recordBtn.setOnAction(event1 -> {
            Experiment.FLIGHT_RECORDING = !Experiment.FLIGHT_RECORDING;
            setBackCol(recordBtn, Experiment.FLIGHT_RECORDING);
        });

        connectButton.setOnAction(event -> {
            CommandInterpreter.executeCommand("run");
        });
        stopButton.setOnAction(event -> {
            ServiceLogger.log("Experiment stopped.");
            Experiment.experimentController.getStopExperiment().set(true);
        });


        centerPane = new VBox();
        labelPane = new VBox();
        buttonPane = new HBox();
        centerPane.getChildren().addAll(inputField);
        labelPane.getChildren().add(simTimeLabel);
        labelPane.getChildren().addAll(labelList);

        buttonPane.getChildren().addAll(flockingBtn, boundaryDetBtn, boundaryTenBtn,
                leaderFollowBtn, thicknessBtn, densityBtn, heightControlBtn, followPathBtn, dimensionBtn, recordBtn, connectButton, stopButton);


        VBox centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);

        AnchorPane root = new AnchorPane();
        root.getChildren().add(centerPane);
        root.getChildren().add(buttonPane);
        root.getChildren().add(textArea);
        root.getChildren().add(labelPane);

        AnchorPane.setBottomAnchor(textArea,25.0);
        AnchorPane.setBottomAnchor(labelPane,25.0);
        AnchorPane.setLeftAnchor(textArea,0.0);
       // AnchorPane.setLeftAnchor(labelPane,505.0);
        AnchorPane.setRightAnchor(textArea,1000.0);
        AnchorPane.setRightAnchor(labelPane,0.0);
        AnchorPane.setTopAnchor(textArea,35.0);
        AnchorPane.setTopAnchor(labelPane,35.0);

        AnchorPane.setBottomAnchor(centerPane,0.0);
        AnchorPane.setLeftAnchor(centerPane,0.0);
        AnchorPane.setRightAnchor(centerPane,0.0);

        AnchorPane.setTopAnchor(buttonPane,5.0);
        AnchorPane.setLeftAnchor(buttonPane, 5.0);
        AnchorPane.setRightAnchor(buttonPane, 5.0);


        root.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER: {
                    Platform.runLater(() -> {
                        String command = inputField.getText();
                        ServiceLogger.log(">> " + command);
                        ServiceLogger.log(CommandInterpreter.executeCommand(command));
                        inputField.setText("");
                    });
                }
                break;
            }
            event.consume();
        });


        Scene scene = new Scene(root, 1400, 400);

        primaryStage.setTitle("V-REP EXPERIMENT CONTROLLER!");
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
