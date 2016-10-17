package cz.cvut.fel.cyber.dca.engine.gui;

import cz.cvut.fel.cyber.dca.engine.core.Swarm;
import cz.cvut.fel.cyber.dca.engine.experiment.ConfigFileLoader;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Jan on 15.10.2016.
 */
public class CommandInterpreter {

    private static Map<String, Function<String, String>> commandMapper = new HashMap<>();

    static{
        commandMapper.put("leader", (arguments) -> {
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
            Swarm.getMembers().get(Integer.valueOf(arguments)).setFailure(true);
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
