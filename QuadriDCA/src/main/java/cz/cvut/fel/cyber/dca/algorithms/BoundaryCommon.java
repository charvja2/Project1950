package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Jan on 13.10.2016.
 */
public class BoundaryCommon {

    public static List<Quadrotor> sortNeighbors(Quadrotor unit){
        return unit.getNeighbors().stream().sorted((a, b) -> {
            Map<Quadrotor, Vector3> relLocMapper = unit.getNeighborRelLocMapper();
            if (Math.atan2(relLocMapper.get(a).toVector2().getY(), relLocMapper.get(a).toVector2().getX())
                    > Math.atan2(relLocMapper.get(b).toVector2().getY(), relLocMapper.get(b).toVector2().getX())) return 1;
            else return -1;
        }).collect(Collectors.toList());
    }

    public static Map<Pair<Quadrotor, Quadrotor>, Double> findEmptySectors(Quadrotor unit){
        List<Quadrotor> neighbors = sortNeighbors(unit);
        Map<Pair<Quadrotor,Quadrotor>,Double> emptySectors = new HashMap<>();
        Map<Quadrotor, Vector3> relLocMapper = unit.getNeighborRelLocMapper();

        System.out.println("Neighbors of " + unit.getId());

        for(int i = 0; i < neighbors.size(); i++){
            Quadrotor neighbor = neighbors.get(i);
            Quadrotor nextNeighbor = (i==neighbors.size()-1) ? neighbors.get(0) : neighbors.get(i+1);

            double neighborAngle = Math.atan2(relLocMapper.get(neighbor).toVector2().getY()
                    , relLocMapper.get(neighbor).toVector2().getX());
            double nextNeighborAngle = Math.atan2(relLocMapper.get(nextNeighbor).toVector2().getY()
                    , relLocMapper.get(nextNeighbor).toVector2().getX());

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
                    unit.setConvexBoundary(true);
                else unit.setConvexBoundary(false);
            }

            if(unit.getNeighborRelLocMapper().get(neighbor).distance(unit.getNeighborRelLocMapper().get(nextNeighbor))
                    > Experiment.ROBOT_COMMUNICATION_RANGE)
                emptySectors.put(neighborPair,angle);
        }
        return emptySectors;
    }

}
