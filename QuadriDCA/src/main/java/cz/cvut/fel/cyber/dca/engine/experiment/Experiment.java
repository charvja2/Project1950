package cz.cvut.fel.cyber.dca.engine.experiment;

import cz.cvut.fel.cyber.dca.engine.gui.ControlGui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Jan on 20.09.2016.
 */
public class Experiment {

    public static int CURRENT_SIMULATION_MILLIS = 0;

    public static int DIMENSION = 3;
    public static double ROBOT_COMMUNICATION_RANGE = 3;         // interaction range
    public static double ROBOT_DESIRED_DISTANCE = 2;            // scale
    public static double ROBOT_SAFETY_ZONE = 1.0;
    public static double ROBOT_MAX_VELOCITY = 0.5;
    public static double LEADER_MAX_VELOCITY = 0.1;
    public static double RATIO = ROBOT_DESIRED_DISTANCE / ROBOT_COMMUNICATION_RANGE ;       // ratio

    // ROBOT_SAFETY_ZONE < ROBOT_DESIRED_DISTANCE < ROBOT_RANGE
    public static int ROBOT_COUNT = 20;
    public static int LEADER_COUNT = 2;
    public static int SIMULATION_STEP_MILLIS = 250;
    public static double FAILURE_RATE_PER_SEC = 1/125;

    // Experiment control flags
    // BASE BEHAVIOR
    public static boolean FLOCKING_ALGORITHM_ACTIVATED = false;
    public static boolean BOID_ALGORITHM_ACTIVATED = true;
    public static boolean BOUNDARY_DETECTION_ALGORITHM_ACTIVATED = true;
    public static boolean BOUNDARY_TENSION_ALGORITHM_ACTIVATED = true;
    // LEADER FORCES
    public static boolean LEADER_FOLLOW_ALGORITHM_ACTIVATED = true;
    // STABILITY IMPROVEMENT
    public static boolean THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED = true;
    public static boolean DENSITY_ALGORITHM_ACTIVATED = true;
    //OTHER ALGORITHMS
    public static boolean LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED = true;
    public static int CHECKPOINT_COUNT = 2;
    public static boolean HEIGHT_SAFETY_CONTROL_ACTIVATED = true;
    public static boolean HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED = true;

    public static double HEIGHT_LAYER_HEIGHT = 1;
    public static double ROBOT_MIN_SAFETY_HEIGHT = 2.0;
    public static double ROBOT_MAX_SAFETY_HEIGHT = 80.0;

    public static String EXPERIMENT_CONFIG_FILENAME = "quadricopters2.txt";

    public static boolean FLIGHT_RECORDING = true;


    public static int simulationTimeMillis = 0;
    public static ExperimentController experimentController;

    public static void main(String args[]) {
        ConfigFileLoader.loadConfig(EXPERIMENT_CONFIG_FILENAME);

        experimentController = new ExperimentController();
        (new Thread(experimentController)).start();

        String[] arguments = {"ControlGui"};
        ControlGui.main(arguments);
    }



}
