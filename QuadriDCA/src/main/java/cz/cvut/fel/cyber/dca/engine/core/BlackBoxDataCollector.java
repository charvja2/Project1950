package cz.cvut.fel.cyber.dca.engine.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 08.10.2016.
 */
public class BlackBoxDataCollector{

    private static String DIR_NAME = "FlightRecords/";
    private static String EXPERIMENT_FILENAME = "experimentRecord";
    private static String POSITION_FILENAME = "positionRecord";
    private static String BOUNDARY_FILENAME = "boundaryRecord";
    private static String CONNECTION_FILENAME = "connectionRecord";
    private static java.nio.file.Path outputPositionFile = Paths.get(DIR_NAME + POSITION_FILENAME + ".txt");
    private static java.nio.file.Path outputBoundaryFile = Paths.get(DIR_NAME + BOUNDARY_FILENAME + ".txt");
    private static java.nio.file.Path outputExperimentFile = Paths.get(DIR_NAME + EXPERIMENT_FILENAME + ".txt");
    private static java.nio.file.Path outputConnectionFile = Paths.get(DIR_NAME + CONNECTION_FILENAME + ".txt");

    private static int iteration = 0;
    private static int skipIterations = 10;

    private static List<String> positionRecords = new ArrayList<>(Swarm.getMembers().size());
    private static List<String> boundaryRecords = new ArrayList<>(Swarm.getMembers().size());
    private static List<String> connectionRecords = new ArrayList<>(Swarm.getMembers().size());


    public static void logUnit(Quadrotor quadracopter){
        positionRecords.add(quadracopter.getId(), quadracopter.exportData());
        boundaryRecords.add(quadracopter.getId(), quadracopter.isBoundary() ? "1" : "0");
        String conns = "";
        String reducedConns = "";
        for(int i = 0; i < Swarm.getMembers().size(); i++){
            if((Swarm.getMembers().get(i).getId()==quadracopter.getId())||(!quadracopter.getNeighbors().contains(Swarm.getMembers().get(i)))){
                conns += "0";

            }else {
                conns += "1";
            }
            if((Swarm.getMembers().get(i).getId()==quadracopter.getId())||(!quadracopter.getReducedNeighbors().contains(Swarm.getMembers().get(i)))){
                reducedConns += "0";

            }else {
                reducedConns += "1";
            }
            if(i!= Swarm.getMembers().size()-1){
                conns += " ";
                reducedConns += " ";
            }

        }

        connectionRecords.add(quadracopter.getId(), conns + System.lineSeparator() + reducedConns);
    }

    public static void logAll(){
        Swarm.getMembers().stream().forEach(BlackBoxDataCollector::logUnit);
    }

    public static List<String> createExperimentInfoLog(){
        List<String> info = new ArrayList<>();
        info.add(Integer.toString(Swarm.getMembers().size()));
        info.add(Integer.toString(Swarm.getLeaders().size()));
        return info;
    }

    private static void clearFiles(){
        File folder = new File(DIR_NAME);
        for(File file : folder.listFiles()){
            if(file.isFile())file.delete();
        }
    }

    public static void writeRecord(){
        if(iteration == 0){

            clearFiles();

            try {
                Files.write(outputExperimentFile, createExperimentInfoLog(), Charset.forName("UTF-8"),StandardOpenOption.CREATE);
            } catch (IOException e) {
                System.out.println("Could not create file.");
                e.printStackTrace();
            }
            try {
                Files.write(outputPositionFile, positionRecords, Charset.forName("UTF-8"),StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                System.out.println("Could not write record.");
                e.printStackTrace();
            }

            try {
                Files.write(outputBoundaryFile, boundaryRecords, Charset.forName("UTF-8"),StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                System.out.println("Could not write record.");
                e.printStackTrace();
            }
            try {
                Files.write(outputConnectionFile, connectionRecords, Charset.forName("UTF-8"),StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                System.out.println("Could not write record.");
                e.printStackTrace();
            }

        }

        if ((iteration%skipIterations)==0) {        // skip some iterations
            try {
                Files.write(outputPositionFile, positionRecords, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.out.println("Could not write record.");
                e.printStackTrace();
            }

            try {
                Files.write(outputBoundaryFile, boundaryRecords, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.out.println("Could not write record.");
                e.printStackTrace();
            }

            try {
                Files.write(outputConnectionFile, connectionRecords, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.out.println("Could not write record.");
                e.printStackTrace();
            }
        }
        iteration++;
    }

}
