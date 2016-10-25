package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.gui.ServiceLogger;
import cz.cvut.fel.cyber.dca.engine.util.Line;
import cz.cvut.fel.cyber.dca.engine.util.MathUtils;
import cz.cvut.fel.cyber.dca.engine.util.Plane;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.collections.SetChangeListener;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.DoubleStream;

import static java.lang.Math.*;

/**
 * Created by Jan on 15.10.2016.
 */
public class BoundaryDetection3D implements Loopable<Quadrotor, Boolean> {

    private static final List<Plane> planes = generatePlanes();

    private static List<Plane> generatePlanes(){
        List<Plane> planes = new ArrayList<>();
        List<Vector3> sphereVectors = new ArrayList<>();

        double phi = 0;
        double theta = 0;
        int step = 10;
        for(int i = 0; i < step; i++){
            phi += 2* PI/step;
            for(int j = 0; j < step/2; j++){
                theta += PI/step;
                sphereVectors.add(new Vector3(Experiment.ROBOT_COMMUNICATION_RANGE*sin(theta)*cos(phi),
                        Experiment.ROBOT_COMMUNICATION_RANGE* sin(theta)*sin(phi),
                        Experiment.ROBOT_COMMUNICATION_RANGE* cos(theta)
                ));
            }
        }

        List<Pair<Vector3,Vector3>> sphereVectorPairList = new ArrayList<>();
        sphereVectors.stream().forEach(vector1 -> sphereVectors.stream().forEach(vector2 -> {
            if(!vector1.equals(vector2)){
                if(!sphereVectorPairList.stream().filter(pair -> pair.getValue().equals(vector1)&&pair.getKey().equals(vector2)).findAny().isPresent())
                    sphereVectorPairList.add(new Pair<>(vector1,vector2));
            }
        }));

        sphereVectorPairList.stream().forEach(line -> planes.add(new Plane(new Vector3(),
                line.getKey(),line.getValue())));

        return planes;
    }

    private boolean isBoundaryX(Quadrotor input){
        Map<Pair<Quadrotor, Quadrotor>, Double> emptySectors = BoundaryCommon.findEmptySectorsX(input);
        if(input.getNeighbors().isEmpty())return true;
        return !emptySectors.isEmpty();
    }

    private boolean isBoundaryY(Quadrotor input){
        Map<Pair<Quadrotor, Quadrotor>, Double> emptySectors = BoundaryCommon.findEmptySectorsY(input);
        if(input.getNeighbors().isEmpty())return true;
        return !emptySectors.isEmpty();
    }

    private boolean isBoundaryZ(Quadrotor input){
        Map<Pair<Quadrotor, Quadrotor>, Double> emptySectors = BoundaryCommon.findEmptySectorsZ(input);
        if(input.getNeighbors().isEmpty())return true;
        return !emptySectors.isEmpty();
    }

    // polygon boundary detection

    private List<Line> getNeighborEdges(Quadrotor unit){
        List<Line> lines = new ArrayList<>();

        unit.getNeighbors().stream().forEach(neighbor -> {
            unit.getNeighbors().stream().forEach(neighbor2 -> {
                if((unit.getRelativeLocalization(neighbor).distance(unit.getRelativeLocalization(neighbor2))
                                                                            < Experiment.ROBOT_COMMUNICATION_RANGE)&&(!neighbor.equals(neighbor2))){
                    /*if(lines.stream().filter(line -> line.getA().equals(unit.getRelativeLocalization(neighbor2))&&
                                        line.getB().equals(unit.getRelativeLocalization(neighbor))).findAny().isPresent())*/

                            lines.add(new Line(unit.getRelativeLocalization(neighbor),unit.getRelativeLocalization(neighbor2)));

                }
            });
            //lines.add(new Line(new Vector3(0,0,0),unit.getRelativeLocalization(neighbor)));
        });

        return lines;
    }

    private boolean getPolygonBoundaryDetection(Quadrotor input){
        List<Line> lines = getNeighborEdges(input);
        List<Plane> planeList = planes;

        for(Plane plane : planeList){
            int intersectionCounter = 0;
            for(Line line : lines){
                if (MathUtils.segmentPlaneIntersection(line, plane)) {
                    intersectionCounter++;
                }
            }
            if((intersectionCounter==0)&&(!lines.isEmpty()))return true;
        }
        return false;
    }

    @Override
    public Boolean loop(Quadrotor input) {
        /*
        input.getBoundaryVector().setX(isBoundaryX(input));
        input.getBoundaryVector().setY(isBoundaryY(input));
        input.getBoundaryVector().setZ(isBoundaryZ(input));
*/
       //return input.getBoundaryVector().and();
        return getPolygonBoundaryDetection(input);
    }
}
