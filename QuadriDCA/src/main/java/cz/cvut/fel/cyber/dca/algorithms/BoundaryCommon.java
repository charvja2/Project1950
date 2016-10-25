package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.util.Plane;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by Jan on 13.10.2016.
 */
public class BoundaryCommon {

    public static List<Quadrotor> sortNeighborsX(Quadrotor unit){
        return unit.getNeighbors().stream().sorted((a, b) -> {
            Map<Quadrotor, Vector3> relLocMapper = unit.getNeighborRelLocMapper();
            if (Math.atan2(relLocMapper.get(a).getZ(), relLocMapper.get(a).getY())
                    > Math.atan2(relLocMapper.get(b).getZ(), relLocMapper.get(b).getY())) return 1;
            else return -1;
        }).collect(Collectors.toList());
    }

    public static List<Quadrotor> sortNeighborsY(Quadrotor unit){
        return unit.getNeighbors().stream().sorted((a, b) -> {
            Map<Quadrotor, Vector3> relLocMapper = unit.getNeighborRelLocMapper();
            if (Math.atan2(relLocMapper.get(a).getZ(), relLocMapper.get(a).getX())
                    > Math.atan2(relLocMapper.get(b).getZ(), relLocMapper.get(b).getX())) return 1;
            else return -1;
        }).collect(Collectors.toList());
    }

    public static List<Quadrotor> sortNeighborsZ(Quadrotor unit){
        return unit.getNeighbors().stream().sorted((a, b) -> {
            Map<Quadrotor, Vector3> relLocMapper = unit.getNeighborRelLocMapper();
            if (Math.atan2(relLocMapper.get(a).getY(), relLocMapper.get(a).getX())
                    > Math.atan2(relLocMapper.get(b).getY(), relLocMapper.get(b).getX())) return 1;
            else return -1;
        }).collect(Collectors.toList());
    }

    public static Map<Pair<Quadrotor, Quadrotor>, Double> findEmptySectorsX(Quadrotor unit){
        List<Quadrotor> neighbors = sortNeighborsX(unit);
        Map<Pair<Quadrotor,Quadrotor>,Double> emptySectors = new HashMap<>();
        Map<Quadrotor, Vector3> relLocMapper = unit.getNeighborRelLocMapper();

        System.out.println("Neighbors of " + unit.getId());

        for(int i = 0; i < neighbors.size(); i++){
            Quadrotor neighbor = neighbors.get(i);
            Quadrotor nextNeighbor = (i==neighbors.size()-1) ? neighbors.get(0) : neighbors.get(i+1);

            double neighborAngle = Math.atan2(relLocMapper.get(neighbor).getZ()
                    , relLocMapper.get(neighbor).getY());
            double nextNeighborAngle = Math.atan2(relLocMapper.get(nextNeighbor).getZ()
                    , relLocMapper.get(nextNeighbor).toVector2().getY());

            Pair<Quadrotor, Quadrotor> neighborPair = new Pair<>(neighbor, nextNeighbor);
            if(neighbor.getId()==nextNeighbor.getId())emptySectors.put(neighborPair,2*Math.PI);

            double angle = (nextNeighborAngle-neighborAngle);
            if(angle<0)angle = 2*Math.PI + angle;

            System.out.println(neighbor.getId() + " --> " + nextNeighbor.getId() + " angle: " + angle);

            if(angle>Math.PI){
                emptySectors.put(neighborPair,angle);
            }

            if(!emptySectors.isEmpty()){
                if(emptySectors.values().stream().filter(angleThanPi -> angleThanPi>Math.PI).findAny().isPresent())
                    unit.getConvexBoundaryVector().setX(true);
                else unit.getConvexBoundaryVector().setX(false);
            }

            if(unit.getNeighborRelLocMapper().get(neighbor).distance(unit.getNeighborRelLocMapper().get(nextNeighbor))
                    > Experiment.ROBOT_COMMUNICATION_RANGE)
                emptySectors.put(neighborPair,angle);
        }
        return emptySectors;
    }

    public static Map<Pair<Quadrotor, Quadrotor>, Double> findEmptySectorsY(Quadrotor unit){
        List<Quadrotor> neighbors = sortNeighborsY(unit);
        Map<Pair<Quadrotor,Quadrotor>,Double> emptySectors = new HashMap<>();
        Map<Quadrotor, Vector3> relLocMapper = unit.getNeighborRelLocMapper();

        System.out.println("Neighbors of " + unit.getId());

        for(int i = 0; i < neighbors.size(); i++){
            Quadrotor neighbor = neighbors.get(i);
            Quadrotor nextNeighbor = (i==neighbors.size()-1) ? neighbors.get(0) : neighbors.get(i+1);

            double neighborAngle = Math.atan2(relLocMapper.get(neighbor).getZ()
                    , relLocMapper.get(neighbor).toVector2().getX());
            double nextNeighborAngle = Math.atan2(relLocMapper.get(nextNeighbor).getZ()
                    , relLocMapper.get(nextNeighbor).toVector2().getX());

            Pair<Quadrotor, Quadrotor> neighborPair = new Pair<>(neighbor, nextNeighbor);
            if(neighbor.getId()==nextNeighbor.getId())emptySectors.put(neighborPair,2*Math.PI);

            double angle = (nextNeighborAngle-neighborAngle);
            if(angle<0)angle = 2*Math.PI + angle;

            if(angle>Math.PI){
                emptySectors.put(neighborPair,angle);
            }

            if(!emptySectors.isEmpty()){
                if(emptySectors.values().stream().filter(angleThanPi -> angleThanPi>Math.PI).findAny().isPresent())
                    unit.getConvexBoundaryVector().setY(true);
                else unit.getConvexBoundaryVector().setY(false);
            }

            if(unit.getNeighborRelLocMapper().get(neighbor).distance(unit.getNeighborRelLocMapper().get(nextNeighbor))
                    > Experiment.ROBOT_COMMUNICATION_RANGE)
                emptySectors.put(neighborPair,angle);
        }
        return emptySectors;
    }

    public static Map<Pair<Quadrotor, Quadrotor>, Double> findEmptySectorsZ(Quadrotor unit){
        List<Quadrotor> neighbors = sortNeighborsZ(unit);
        Map<Pair<Quadrotor,Quadrotor>,Double> emptySectors = new HashMap<>();
        Map<Quadrotor, Vector3> relLocMapper = unit.getNeighborRelLocMapper();

        for(int i = 0; i < neighbors.size(); i++){
            Quadrotor neighbor = neighbors.get(i);
            Quadrotor nextNeighbor = (i==neighbors.size()-1) ? neighbors.get(0) : neighbors.get(i+1);

            double neighborAngle = Math.atan2(relLocMapper.get(neighbor).getY()
                    , relLocMapper.get(neighbor).toVector2().getX());
            double nextNeighborAngle = Math.atan2(relLocMapper.get(nextNeighbor).getY()
                    , relLocMapper.get(nextNeighbor).toVector2().getX());

            Pair<Quadrotor, Quadrotor> neighborPair = new Pair<>(neighbor, nextNeighbor);
            if(neighbor.getId()==nextNeighbor.getId())emptySectors.put(neighborPair,2*Math.PI);

            double angle = (nextNeighborAngle-neighborAngle);
            if(angle<0)angle = 2*Math.PI + angle;

            if(angle>Math.PI){
                emptySectors.put(neighborPair,angle);
            }

            if(!emptySectors.isEmpty()){
                if(emptySectors.values().stream().filter(angleThanPi -> angleThanPi>Math.PI).findAny().isPresent())
                    unit.getConvexBoundaryVector().setZ(true);
                else unit.getConvexBoundaryVector().setZ(false);
            }

            if(unit.getNeighborRelLocMapper().get(neighbor).distance(unit.getNeighborRelLocMapper().get(nextNeighbor))
                    > Experiment.ROBOT_COMMUNICATION_RANGE)
                emptySectors.put(neighborPair,angle);
        }
        return emptySectors;
    }

}
