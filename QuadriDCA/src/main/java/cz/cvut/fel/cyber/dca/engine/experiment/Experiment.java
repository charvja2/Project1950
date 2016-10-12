package cz.cvut.fel.cyber.dca.engine.experiment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Jan on 20.09.2016.
 */
public class Experiment {

    public static double ROBOT_COMMUNICATION_RANGE = 3;
    public static double ROBOT_SAFETY_ZONE = 1.0;
    public static double ROBOT_MAX_VELOCITY = 0.5;
    public static double LEADER_MAX_VELOCITY = 0.1;
    public static double ROBOT_DESIRED_DISTANCE = 2;
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

    public static double ROBOT_MIN_SAFETY_HEIGHT = 2.0;
    public static double ROBOT_MAX_SAFETY_HEIGHT = 80.0;


    public static String EXPERIMENT_CONFIG_FILENAME = "multipleQuadricopters.txt";
    //public static String EXPERIMENT_CONFIG_FILENAME = "tri.txt";

    public static boolean FLIGHT_RECORDING = true;

    private static void loadConfigFile(String filename){
        try {
            Files.lines(Paths.get(filename)).forEach(line ->{
                if(line.contains("=")){
                    String[] param = line.split(" = ");
                    String key = param[0];
                    String value = param[1];

                    if(key.equals("ROBOT_COMMUNICATION_RANGE")){
                        ROBOT_COMMUNICATION_RANGE = Double.parseDouble(value);
                    }else if(key.equals("ROBOT_SAFETY_ZONE")){
                        ROBOT_SAFETY_ZONE = Double.parseDouble(value);
                    }else if(key.equals("ROBOT_MAX_VELOCITY")){
                        ROBOT_MAX_VELOCITY = Double.parseDouble(value);
                    }else if(key.equals("LEADER_MAX_VELOCITY")){
                        LEADER_MAX_VELOCITY = Double.parseDouble(value);
                    }else if(key.equals("ROBOT_DESIRED_DISTANCE")){
                        ROBOT_DESIRED_DISTANCE = Double.parseDouble(value);
                    }else if(key.equals("ROBOT_COUNT")){
                        ROBOT_COUNT = Integer.parseInt(value);
                    }else if(key.equals("LEADER_COUNT")){
                        LEADER_COUNT = Integer.parseInt(value);
                    }else if(key.equals("SIMULATION_STEP_MILLIS")){
                        SIMULATION_STEP_MILLIS = Integer.parseInt(value);
                    }else if(key.equals("FAILURE_RATE_PER_SEC")){
                        FAILURE_RATE_PER_SEC = Double.parseDouble(value.split("/")[0])/Double.parseDouble(value.split("/")[1]);
                    }else if(key.equals("BOID_ALGORITHM_ACTIVATED")){
                        BOID_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                    }else if(key.equals("FLOCKING_ALGORITHM_ACTIVATED")){
                        FLOCKING_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                    }else if(key.equals("BOUNDARY_DETECTION_ALGORITHM_ACTIVATED")){
                        BOUNDARY_DETECTION_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                    }else if(key.equals("BOUNDARY_TENSION_ALGORITHM_ACTIVATED")){
                        BOUNDARY_TENSION_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                    }else if(key.equals("LEADER_FOLLOW_ALGORITHM_ACTIVATED")){
                        LEADER_FOLLOW_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                    }else if(key.equals("THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED")){
                        THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                    }else if(key.equals("DENSITY_ALGORITHM_ACTIVATED")){
                        DENSITY_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                    }else if(key.equals("LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED")){
                        LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED = Boolean.parseBoolean(value);
                    }else if(key.equals("CHECKPOINT_COUNT")){
                        CHECKPOINT_COUNT = Integer.parseInt(value);
                    }else{
                        if(!key.contains("//"))System.out.println("Unknown parameter: " + line);
                    }

                }

            });
        } catch (IOException e) {
            System.out.println("Failed to load config file!\n");
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        loadConfigFile("ExperimentConfig/" + EXPERIMENT_CONFIG_FILENAME);

        ExperimentController controller = new ExperimentController();

        (new Thread(controller)).start();
    }



}
