package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.gui.ServiceLogger;
import cz.cvut.fel.cyber.dca.engine.util.*;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static cz.cvut.fel.cyber.dca.engine.util.Vector3.*;
import static java.lang.Math.*;

/**
 * Created by Jan on 15.10.2016.
 */
public class BoundaryDetection3D implements Loopable<Quadrotor, Boolean> {

    private static final List<Line> lines = generateLines();
    private static final List<Line> specialLines = generateSpecialLines();

    private static List<Vector3> generateSphereVectors(){
        List<Vector3> sphereVectors = new ArrayList<>();

        double phi = 0;
        double theta = 0;
        int step = 60;
        for(int i = 0; i < step; i++){
            phi += 2* PI/step;
            for(int j = 0; j < step/2; j++){
                theta += 2*PI/step;
                sphereVectors.add(new Vector3(Experiment.ROBOT_COMMUNICATION_RANGE*sin(theta)*cos(phi),
                        Experiment.ROBOT_COMMUNICATION_RANGE* sin(theta)*sin(phi),
                        Experiment.ROBOT_COMMUNICATION_RANGE* cos(theta)
                ));
            }
        }
        return sphereVectors;
    }

    private static List<Line> generateLines(){
        List<Line> lines = new ArrayList<>();
        List<Vector3> sphereVectors = generateSphereVectors();

        return sphereVectors.stream().map(vec -> new Line(new Vector3(),vec)).collect(Collectors.toList());
    }

    private static List<Line> generateSpecialLines(){
        List<Line> specialLines = new ArrayList<>();
        specialLines.add(new Line(new Vector3(-Experiment.ROBOT_COMMUNICATION_RANGE,0,0)));
        specialLines.add(new Line(new Vector3(Experiment.ROBOT_COMMUNICATION_RANGE,0,0)));
        specialLines.add(new Line(new Vector3(0,Experiment.ROBOT_COMMUNICATION_RANGE,0)));
        specialLines.add(new Line(new Vector3(0,-Experiment.ROBOT_COMMUNICATION_RANGE,0)));
        specialLines.add(new Line(new Vector3(0,0,Experiment.ROBOT_COMMUNICATION_RANGE)));
        specialLines.add(new Line(new Vector3(0,0,-Experiment.ROBOT_COMMUNICATION_RANGE)));
        return specialLines;
    }

    private static List<Plane> generateNeighborPlanes(Quadrotor input){
        Set<Quadrotor> neighbors = input.getNeighbors();
        List<Plane> planeList = new ArrayList<>();
        List<Triplet<Quadrotor>> triplets = new ArrayList<>();

        for(Quadrotor neighbor : neighbors){
            for(Quadrotor neighbor1 : neighbors) {
                for (Quadrotor neighbor2 : neighbors) {
                    if((!neighbor1.equals(neighbor2))&&(neighbor1.getDistance(neighbor2)<Experiment.ROBOT_COMMUNICATION_RANGE)){
                        if(!(triplets.stream().filter(trip -> trip.containsAll(neighbor,neighbor1,neighbor2)).findAny().isPresent())){
                            triplets.add(new Triplet<>(neighbor,neighbor1,neighbor2));
                            planeList.add(new Plane(input.getRelativeLocalization(neighbor),neighbor.getRelativeLocalization(neighbor1),neighbor.getRelativeLocalization(neighbor2)));
                        }
                    }
                }
            }
        }

        return planeList;
    }

    private int getIntersectionCount(Line line, List<Plane> planes){
        int intersectionCounter = 0;
        for(Plane plane : planes){
            if(MathUtils.segmentLineSegmentPlaneIntersection(line,plane)) intersectionCounter++;
        }
        return intersectionCounter;
    }

    private boolean testSegmentsTrianglesIntersection(Quadrotor input, List<Line> lines, List<Plane> planes){
        for(Line line : lines){
            int intersections = getIntersectionCount(line,planes);
            if(intersections==0){
                input.setLastIntersectionLine(line);
                return true;
            }
        }
        return false;
    }

    private boolean isBoundary(Quadrotor input){
        if(input.getNeighbors().size()<3)return true;

        List<Plane> planes = generateNeighborPlanes(input);

        if(input.getLastIntersectionLine()!=null){
            if(getIntersectionCount(input.getLastIntersectionLine(),planes)==0){
                return true;
            }
        }

        if(testSegmentsTrianglesIntersection(input,specialLines,planes))return true;
        if(testSegmentsTrianglesIntersection(input,lines,planes))return true;

        return false;
    }

    @Override
    public Boolean loop(Quadrotor input) {
        return isBoundary(input);
    }
}
