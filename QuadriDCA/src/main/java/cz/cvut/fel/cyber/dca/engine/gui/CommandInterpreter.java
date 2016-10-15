package cz.cvut.fel.cyber.dca.engine.gui;

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
        commandMapper.put("stop", (comm) -> {
            Experiment.experimentController.getStopExperiment().set(true);
            return "Experiment stopped.";
        });
        commandMapper.put("run", (comm) -> {
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
