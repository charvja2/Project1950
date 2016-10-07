package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadracopter;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Jan on 28. 10. 2015.
 */
public class BoundaryDetection implements Loopable<Quadracopter, Boolean> {

    private List<Quadracopter> sortNeighbors(Quadracopter unit){
        return unit.getNeighbors().stream().sorted((a, b) -> {
            Map<Quadracopter, Vector3> relLocMapper = unit.getNeighborRelLocMapper();
            if (Math.atan2(relLocMapper.get(a).toVector2().getX(), relLocMapper.get(a).toVector2().getY())
                    > Math.atan2(relLocMapper.get(b).toVector2().getX(), relLocMapper.get(b).toVector2().getY())) return 1;
            else return -1;
        }).collect(Collectors.toList());
    }

    private Map<Pair<Quadracopter, Quadracopter>, Double> findEmptySectors(Quadracopter unit){
        List<Quadracopter> neighbors = sortNeighbors(unit);
        Map<Pair<Quadracopter,Quadracopter>,Double> emptySectors = new HashMap<>();
        Map<Quadracopter, Vector3> relLocMapper = unit.getNeighborRelLocMapper();

        System.out.println("Neighbors of " + unit.getId());

        for(int i = 0; i < neighbors.size(); i++){
            Quadracopter neighbor = neighbors.get(i);
            Quadracopter nextNeighbor = (i==neighbors.size()-1) ? neighbors.get(0) : neighbors.get(i+1);

            double neighborAngle = Math.atan2(relLocMapper.get(neighbor).toVector2().getX()
                                                                , relLocMapper.get(neighbor).toVector2().getY());
            double nextNeighborAngle = Math.atan2(relLocMapper.get(nextNeighbor).toVector2().getX()
                    , relLocMapper.get(nextNeighbor).toVector2().getY());

            Pair<Quadracopter, Quadracopter> neighborPair = new Pair<>(neighbor, nextNeighbor);
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
        //System.out.println("Robot " + unit.getId() + " is boundary: " + !emptySectors.isEmpty());
        return emptySectors;
    }


    @Override
    public Boolean loop(Quadracopter input) {
        Map<Pair<Quadracopter, Quadracopter>, Double> emptySectors = findEmptySectors(input);
        if(input.getNeighbors().isEmpty())return true;
        return !emptySectors.isEmpty();
    }
}
