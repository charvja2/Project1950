package cz.cvut.fel.cyber.dca.engine.gui;

import cz.cvut.fel.cyber.dca.algorithms.AlgorithmLibrary;
import cz.cvut.fel.cyber.dca.engine.core.Swarm;
import cz.cvut.fel.cyber.dca.engine.experiment.ConfigFileLoader;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;

import java.awt.*;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Jan on 15.10.2016.
 */
public class CommandInterpreter {

    private static Map<String, Function<String, String>> commandMapper = new HashMap<>();

    static{
        commandMapper.put("path", (arguments) -> {
            if(arguments.contains("true")||arguments.contains("false")){
                Experiment.LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED = Boolean.parseBoolean(arguments);
                ServiceLogger.log("Follow path algorithm activated: " + Experiment.LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED + ".");
                ControlGui.setBackCol(ControlGui.followPathBtn,Experiment.LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED);
                return "";
            }else if(arguments.contains("save")){
                String[] args = arguments.split(" ");
                ConfigFileLoader.savePath(Swarm.getPath3DList().get(Integer.parseInt(args[1])));
                return "Path " + args[1] + " save as: " + args[1] + ".txt";
            }else if(arguments.contains("load")){
                String[] args = arguments.split(" ");
                ConfigFileLoader.loadPath(args[1] + ".txt");
                return "Path " + args[1] + ".txt loaded.";
            }else if(arguments.contains("param")){
                Double originValue = AlgorithmLibrary.getFollowPathAlgorithm().getParam();
                AlgorithmLibrary.getFollowPathAlgorithm().setParam(Double.parseDouble(arguments.split(" ")[1]));
                ServiceLogger.log("Follow path algorithm wight set to: " + arguments.split(" ")[1]+ ". ( Original: " + originValue + " )" );
            }
            return "Unknown param: " + arguments + ".";
        });
        commandMapper.put("clear", (arguments) -> {
            ServiceLogger.clear();
            return "";

        });
        commandMapper.put("range", (arguments) -> {
            Double originValue = Experiment.ROBOT_COMMUNICATION_RANGE;
            Experiment.ROBOT_COMMUNICATION_RANGE = Double.parseDouble(arguments.split(" ")[1]);
            Experiment.RATIO = Experiment.ROBOT_DESIRED_DISTANCE / Experiment.ROBOT_COMMUNICATION_RANGE;
            ServiceLogger.log("Robot communication range set to: " + arguments.split(" ")[1]+ ". ( Original: " + originValue + " )" );
            return "";

        });
        commandMapper.put("flock", (arguments) -> {
            Double originValue = Experiment.ROBOT_DESIRED_DISTANCE;
            Experiment.ROBOT_DESIRED_DISTANCE = Double.parseDouble(arguments.split(" ")[1]);
            Experiment.RATIO = Experiment.ROBOT_DESIRED_DISTANCE / Experiment.ROBOT_COMMUNICATION_RANGE;
            ServiceLogger.log("Robot desired distance set to: " + arguments.split(" ")[1]+ ". ( Original: " + originValue + " )" );
            return "";
        });
        commandMapper.put("flock", (arguments) -> {
            if(arguments.contains("true")||arguments.contains("false")){
                Experiment.FLOCKING_ALGORITHM_ACTIVATED = Boolean.parseBoolean(arguments);
                ServiceLogger.log("Flocking algorithm activated: " + Experiment.FLOCKING_ALGORITHM_ACTIVATED + ".");
                ControlGui.setBackCol(ControlGui.flockingBtn,Experiment.FLOCKING_ALGORITHM_ACTIVATED);
                return "";
            }else if(arguments.split(" ")[0].equals("param")){
                Double originValue = AlgorithmLibrary.getFlockingAlgorithm().getParam();
                AlgorithmLibrary.getFlockingAlgorithm().setParam(Double.parseDouble(arguments.split(" ")[1]));
                ServiceLogger.log("Flocking algorithm weight set to: " + arguments.split(" ")[1]+ ". ( Original: " + originValue + " )" );
                return "";
            }
            return "Unknown param: " + arguments + ".";
        });
        commandMapper.put("bounddet", (arguments) -> {
            if(arguments.contains("true")||arguments.contains("false")){
                Experiment.BOUNDARY_DETECTION_ALGORITHM_ACTIVATED = Boolean.parseBoolean(arguments);
                ServiceLogger.log("Boundary detection algorithm activated: " + Experiment.BOUNDARY_DETECTION_ALGORITHM_ACTIVATED + ".");
                ControlGui.setBackCol(ControlGui.boundaryDetBtn,Experiment.BOUNDARY_DETECTION_ALGORITHM_ACTIVATED);

                return "";
            }
            return "Unknown param: " + arguments + ".";
        });
        commandMapper.put("boundten", (arguments) -> {
            if(arguments.contains("true")||arguments.contains("false")){
                Experiment.BOUNDARY_TENSION_ALGORITHM_ACTIVATED = Boolean.parseBoolean(arguments);
                ServiceLogger.log("Boundary tension algorithm activated: " + Experiment.BOUNDARY_TENSION_ALGORITHM_ACTIVATED + ".");
                ControlGui.setBackCol(ControlGui.boundaryTenBtn,Experiment.BOUNDARY_TENSION_ALGORITHM_ACTIVATED);
                return "";
            }else if(arguments.split(" ")[0].equals("param")){
                Double originValue = 0d;
                if(Experiment.DIMENSION == 2) {
                    originValue = AlgorithmLibrary.getBoundaryTensionAlgorithm().getwParam();
                    AlgorithmLibrary.getBoundaryTensionAlgorithm().setwParam(Double.parseDouble(arguments.split(" ")[1]));
                }else {
                    originValue = AlgorithmLibrary.getBoundaryTension3DAlgorithm().getwParam();
                    AlgorithmLibrary.getBoundaryTension3DAlgorithm().setwParam(Double.parseDouble(arguments.split(" ")[1]));
                }
                ServiceLogger.log("Boundary tension algorithm weight set to: " + arguments.split(" ")[1]+ ". ( Original: " + originValue + " )");
                return "";
            }
            return "Unknown param: " + arguments + ".";
        });
        commandMapper.put("leader", (arguments) -> {
            if(arguments.contains("true")||arguments.contains("false")){
                Experiment.LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED = Boolean.parseBoolean(arguments);
                ServiceLogger.log("Leader follow algorithm activated: " + Experiment.LEADER_FOLLOW_ALGORITHM_ACTIVATED + ".");
                ControlGui.setBackCol(ControlGui.leaderFollowBtn,Experiment.LEADER_FOLLOW_ALGORITHM_ACTIVATED);

                return "";
            }else if(arguments.split(" ")[0].equals("param")){
                Double originValue = AlgorithmLibrary.getLeaderFollowAlgorithm().getParam();
                AlgorithmLibrary.getLeaderFollowAlgorithm().setParam(Double.parseDouble(arguments.split(" ")[1]));
                ServiceLogger.log("Leader follow algorithm weight set to: " + arguments.split(" ")[1]+ ". ( Original: " + originValue + " )");
                return "";
            }
            return "Unknown param: " + arguments + ".";
        });

        commandMapper.put("density", (arguments) -> {
            if(arguments.contains("true")||arguments.contains("false")){
                Experiment.DENSITY_ALGORITHM_ACTIVATED = Boolean.parseBoolean(arguments);
                ServiceLogger.log("Density algorithm activated: " + Experiment.DENSITY_ALGORITHM_ACTIVATED + ".");
                ControlGui.setBackCol(ControlGui.densityBtn,Experiment.DENSITY_ALGORITHM_ACTIVATED);
                return "";
            }else if(arguments.split(" ")[0].equals("param")){
                Double originValue = AlgorithmLibrary.getDensityAlgorithm().getParam();
                AlgorithmLibrary.getDensityAlgorithm().setParam(Double.parseDouble(arguments.split(" ")[1]));
                ServiceLogger.log("Density algorithm weight set to: " + arguments.split(" ")[1]+ ". ( Original: " + originValue + " )" );
                return "";
            }
            return "Unknown param: " + arguments + ".";
        });
        commandMapper.put("thickness", (arguments) -> {
            if(arguments.contains("true")||arguments.contains("false")){
                Experiment.THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED = Boolean.parseBoolean(arguments);
                ServiceLogger.log("Thickness contraction algorithm activated: " + Experiment.THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED + ".");
                ControlGui.setBackCol(ControlGui.thicknessBtn,Experiment.THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED);
                return "";
            }else if(arguments.split(" ")[0].equals("param")){
                Double originValue = AlgorithmLibrary.getThicknessDeterminationNContractionAlgorithm().getParam();
                AlgorithmLibrary.getThicknessDeterminationNContractionAlgorithm().setParam(Double.parseDouble(arguments.split(" ")[1]));
                ServiceLogger.log("Thickness contraction algorithm weight set to: " + arguments.split(" ")[1]+ ". ( Original: " + originValue + " )" );
                return "";
            }
            return "Unknown param: " + arguments + ".";
        });

        commandMapper.put("height", (arguments) -> {
            if(arguments.contains("true")||arguments.contains("false")){
                Experiment.HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED = Boolean.parseBoolean(arguments);
                ServiceLogger.log("Height control algorithm activated: " + Experiment.HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED + ".");
                ControlGui.setBackCol(ControlGui.heightControlBtn,Experiment.HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED);
                return "";
            }else if(arguments.split(" ")[0].equals("param")){
                Double originValue = AlgorithmLibrary.getHeightLayerControlAlgorithm().getParam();
                AlgorithmLibrary.getHeightLayerControlAlgorithm().setParam(Double.parseDouble(arguments.split(" ")[1]));
                ServiceLogger.log("Height algorithm weight set to: " + arguments.split(" ")[1]+ ". ( Original: " + originValue + " )" );
                return "";
            }
            return "Unknown param: " + arguments + ".";
        });

        commandMapper.put("leaderCount", (arguments) -> {
            Swarm.getMembers().stream().forEach(member -> {
                if(member.getId()<Integer.valueOf(arguments))member.setLeader(true);
                else member.setLeader(false);
            });
            return "Leader count set to " + arguments + ".";
        });
        commandMapper.put("log", (arguments) -> {
            ServiceLogger.LOG_ON = Boolean.valueOf(arguments);
            return "Log turned on: " + ServiceLogger.LOG_ON + ".";
        });
        commandMapper.put("save", (arguments) -> {
            String realArg = arguments;
            if(arguments.isEmpty()) realArg = Experiment.EXPERIMENT_CONFIG_FILENAME;
            ConfigFileLoader.saveConfig(realArg);
            return "Config file " + realArg + " saved.";
        });
        commandMapper.put("load", (arguments) -> {
            String realArg = arguments;
            if(arguments.isEmpty()) realArg = Experiment.EXPERIMENT_CONFIG_FILENAME;
            ConfigFileLoader.loadConfig(realArg);
            return "Config file " + realArg + " loaded.";
        });
        commandMapper.put("config", (arguments) -> {
            Experiment.EXPERIMENT_CONFIG_FILENAME = arguments;
            return "Experiment config filename changed: " + arguments;
        });
        commandMapper.put("help", (arguments) -> {
            commandMapper.entrySet().stream().forEach(set -> ServiceLogger.log(set.getKey()));
            return "Available commands:";
        });
        commandMapper.put("failure", (arguments) -> {
            if(arguments.contains("auto")){
                String[] args = arguments.split(" ");
                Experiment.AUTO_FAILURE = Boolean.parseBoolean(args[1]);
                ServiceLogger.log("Auto failure set to: " + Experiment.AUTO_FAILURE+ " (rate: "
                                                                 + Experiment.FAILURE_RATE_PER_SEC + "/sec.");
            }else Swarm.getMembers().get(Integer.valueOf(arguments)).setFailure(true);
            return "Quadrotor " + arguments + " failed.";
        });
        commandMapper.put("land", (arguments) -> {
            Swarm.getMembers().get(Integer.valueOf(arguments)).setLandingCmd(true);
            return "Quadrotor " + arguments + " is landing.";
        });
        commandMapper.put("takeoff", (arguments) -> {
            Swarm.getMembers().get(Integer.valueOf(arguments)).setTakeOffCmd(true);
            if(Swarm.getMembers().get(Integer.valueOf(arguments)).isLandingCmd())
                    Swarm.getMembers().get(Integer.valueOf(arguments)).setLandingCmd(false);
            return "Quadrotor " + arguments + "is taking off.";
        });
        commandMapper.put("stop", (arguments) -> {
            Experiment.experimentController.getStopExperiment().set(true);
            return "Experiment stopped.";
        });
        commandMapper.put("run", (arguments) -> {
            String[] args = {"Experiment"};
            Experiment.main(args);
            return "Experiment started.";
        });
    }

    public static String executeCommand(String command){
        String[] frags = command.split(" ");
        command = frags[0];
        String arguments = "";
        for(int i = 1 ; i < frags.length; i++){
            arguments+=frags[i];
            if(i!=frags.length-1)arguments+=" ";
        }
        if(commandMapper.containsKey(command.toLowerCase())){
            return commandMapper.get(command.toLowerCase()).apply(arguments.toLowerCase());
        }else return "Unknown command: " + command;
    }

}
