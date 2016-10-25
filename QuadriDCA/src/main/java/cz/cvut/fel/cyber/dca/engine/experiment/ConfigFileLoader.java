package cz.cvut.fel.cyber.dca.engine.experiment;

import cz.cvut.fel.cyber.dca.engine.core.Path3D;
import cz.cvut.fel.cyber.dca.engine.gui.ControlGui;
import cz.cvut.fel.cyber.dca.engine.gui.ServiceLogger;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.*;

/**
 * Created by Jan on 16.10.2016.
 */
public class ConfigFileLoader {

    private static String CONFIG_DIR = "ExperimentConfig/";
    private static String PATH_DIR = "Path/";


    public static void saveConfig(String configFilename){
        List<String> configList = new ArrayList<>();
        configList.add("DIMENSION="+Integer.toString(DIMENSION));
        configList.add("ROBOT_COMMUNICATION_RANGE="+Double.toString(ROBOT_COMMUNICATION_RANGE));
        configList.add("ROBOT_DESIRED_DISTANCE="+Double.toString(ROBOT_DESIRED_DISTANCE));
        configList.add("ROBOT_MAX_VELOCITY="+Double.toString(ROBOT_MAX_VELOCITY));
        configList.add("LEADER_MAX_VELOCITY="+Double.toString(LEADER_MAX_VELOCITY));
        configList.add("ROBOT_COUNT="+Integer.toString(ROBOT_COUNT));
        configList.add("LEADER_COUNT="+Integer.toString(LEADER_COUNT));
        configList.add("SIMULATION_STEP_MILLIS="+Integer.toString(SIMULATION_STEP_MILLIS));
        configList.add("FAILURE_RATE_PER_SEC="+Double.toString(FAILURE_RATE_PER_SEC));
        configList.add("FLOCKING_ALGORITHM_ACTIVATED="+Boolean.toString(FLOCKING_ALGORITHM_ACTIVATED));
        configList.add("BOUNDARY_DETECTION_ALGORITHM_ACTIVATED="+Boolean.toString(BOUNDARY_DETECTION_ALGORITHM_ACTIVATED));
        configList.add("BOUNDARY_TENSION_ALGORITHM_ACTIVATED="+Boolean.toString(BOUNDARY_TENSION_ALGORITHM_ACTIVATED));
        configList.add("LEADER_FOLLOW_ALGORITHM_ACTIVATED="+Boolean.toString(LEADER_FOLLOW_ALGORITHM_ACTIVATED));
        configList.add("THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED="+Boolean.toString(THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED));
        configList.add("DENSITY_ALGORITHM_ACTIVATED="+Boolean.toString(DENSITY_ALGORITHM_ACTIVATED));
        configList.add("LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED="+Boolean.toString(LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED));
        configList.add("HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED="+Boolean.toString(HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED));
        configList.add("FLIGHT_RECORDING="+Boolean.toString(FLIGHT_RECORDING));
        configList.add("HEIGHT_LAYER_HEIGHT="+Double.toString(HEIGHT_LAYER_HEIGHT));

        try {
            Files.write(Paths.get(CONFIG_DIR + configFilename), configList, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.out.println("Could not create file.");
            e.printStackTrace();
        }

    }

    public static void loadConfig(String configFilename) {
        try {   Files.lines(Paths.get(CONFIG_DIR + configFilename)).forEach(line -> {
                if (line.contains("=")) {
                    String[] param = line.split("=");
                    String key = param[0].trim();
                    String value = param[1].trim();

                    if (key.equals("ROBOT_COMMUNICATION_RANGE")) {
                        ROBOT_COMMUNICATION_RANGE = Double.parseDouble(value);
                    } else if (key.equals("ROBOT_SAFETY_ZONE")) {
                        ROBOT_SAFETY_ZONE = Double.parseDouble(value);
                    } else if (key.equals("ROBOT_MAX_VELOCITY")) {
                        ROBOT_MAX_VELOCITY = Double.parseDouble(value);
                    } else if (key.equals("LEADER_MAX_VELOCITY")) {
                        LEADER_MAX_VELOCITY = Double.parseDouble(value);
                    } else if (key.equals("ROBOT_DESIRED_DISTANCE")) {
                        ROBOT_DESIRED_DISTANCE = Double.parseDouble(value);
                    } else if (key.equals("ROBOT_COUNT")) {
                        ROBOT_COUNT = Integer.parseInt(value);
                    } else if (key.equals("LEADER_COUNT")) {
                        LEADER_COUNT = Integer.parseInt(value);
                    } else if (key.equals("SIMULATION_STEP_MILLIS")) {
                        SIMULATION_STEP_MILLIS = Integer.parseInt(value);
                    } else if (key.equals("FAILURE_RATE_PER_SEC")) {
                        if(value.contains("/")) FAILURE_RATE_PER_SEC = Double.parseDouble(value.split("/")[0]) / Double.parseDouble(value.split("/")[1]);
                        else FAILURE_RATE_PER_SEC = Double.parseDouble(value);
                    } else if (key.equals("FLOCKING_ALGORITHM_ACTIVATED")) {
                        FLOCKING_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                        ControlGui.setBackCol(ControlGui.flockingBtn,Experiment.FLOCKING_ALGORITHM_ACTIVATED);
                    } else if (key.equals("BOUNDARY_DETECTION_ALGORITHM_ACTIVATED")) {
                        BOUNDARY_DETECTION_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                        ControlGui.setBackCol(ControlGui.boundaryDetBtn,Experiment.BOUNDARY_DETECTION_ALGORITHM_ACTIVATED);
                    } else if (key.equals("BOUNDARY_TENSION_ALGORITHM_ACTIVATED")) {
                        BOUNDARY_TENSION_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                        ControlGui.setBackCol(ControlGui.boundaryTenBtn,Experiment.BOUNDARY_TENSION_ALGORITHM_ACTIVATED);
                    } else if (key.equals("LEADER_FOLLOW_ALGORITHM_ACTIVATED")) {
                        LEADER_FOLLOW_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                        ControlGui.setBackCol(ControlGui.leaderFollowBtn,Experiment.LEADER_FOLLOW_ALGORITHM_ACTIVATED);
                    } else if (key.equals("THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED")) {
                        THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                        ControlGui.setBackCol(ControlGui.thicknessBtn,Experiment.THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED);
                    } else if (key.equals("DENSITY_ALGORITHM_ACTIVATED")) {
                        DENSITY_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                        ControlGui.setBackCol(ControlGui.densityBtn,Experiment.DENSITY_ALGORITHM_ACTIVATED);
                    } else if (key.equals("LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED")) {
                        LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED = Boolean.parseBoolean(value);
                        ControlGui.setBackCol(ControlGui.followPathBtn,Experiment.LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED);
                    }else if (key.equals("DIMENSION")) {
                        DIMENSION = Integer.parseInt(value);
                    }else if (key.equals("HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED")) {
                        HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED = Boolean.parseBoolean(value);
                        ControlGui.setBackCol(ControlGui.heightControlBtn,Experiment.HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED);
                    }else if (key.equals("HEIGHT_LAYER_HEIGHT")) {
                        HEIGHT_LAYER_HEIGHT = Double.parseDouble(value);
                    }else if (key.equals("FLIGHT_RECORDING")) {
                        FLIGHT_RECORDING = Boolean.parseBoolean(value);
                        ControlGui.setBackCol(ControlGui.recordBtn,Experiment.FLIGHT_RECORDING);
                    }else {
                        if (!key.contains("//")) System.out.println("Unknown parameter: " + line);
                    }

                }

            });
        } catch (IOException e) {
            System.out.println("Failed to load config file!\n");
            e.printStackTrace();
        }
    }

    public static Path3D loadPath(String filename) {
        List<Vector3> vecList = new ArrayList<>();
        try {
            Files.lines(Paths.get(PATH_DIR + filename)).forEach(line -> {
                String[] position = line.split(" ");
                vecList.add(new Vector3(Double.parseDouble(position[0]),Double.parseDouble(position[1]),Double.parseDouble(position[2])));
            });
        } catch (IOException e) {
            ServiceLogger.log("Path file: "+ PATH_DIR + filename + " could not be opened.");
            e.printStackTrace();
        }
        return new Path3D(Integer.parseInt(filename.split("\\.")[0]), vecList);
    }

    public static void savePath(Path3D path){
        try {
            List<String> pathList = path.getPath().stream().map(vec ->
                    String.valueOf(vec.getX()) + " " + String.valueOf(vec.getY()) + " " + String.valueOf(vec.getZ())).
                    collect(Collectors.toList());
            Files.write(Paths.get(PATH_DIR + path.getIndex()+ ".txt"),
                    pathList,
                    Charset.forName("UTF-8"),
                    StandardOpenOption.CREATE);
        } catch (IOException e) {
            ServiceLogger.log("Could not save path to file: " + PATH_DIR + path.getIndex()+ ".txt");
            e.printStackTrace();
        }

    }

}
