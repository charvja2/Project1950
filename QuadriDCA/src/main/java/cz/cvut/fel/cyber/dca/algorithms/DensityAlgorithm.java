package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.util.MathUtils;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;

import java.util.Map;
import java.util.Optional;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.*;

/**
 * Created by Jan on 12. 12. 2015.
 */
public class DensityAlgorithm implements Loopable<Quadrotor, Vector3> {

    private double param = 0.5;

    private double calculateObservableArea(Quadrotor unit){
        if (!unit.isBoundary()){
            if(DIMENSION == 2) return MathUtils.ringSurface(ROBOT_COMMUNICATION_RANGE);
            if(DIMENSION == 3) return MathUtils.sphereVolume(ROBOT_COMMUNICATION_RANGE);
        }
        else {
            if(DIMENSION == 2){
                Map<Pair<Quadrotor,Quadrotor>,Double> emptySectors = BoundaryCommon.findEmptySectors(unit);

                Optional<Map.Entry<Pair<Quadrotor,Quadrotor>,Double>> optSector = emptySectors.entrySet().stream()
                        .max((a, b) -> (a.getValue()>b.getValue()) ? 1 : -1);

                Double angle = 0d;
                if (optSector.isPresent()) angle = optSector.get().getValue();

                double invisibleArea = MathUtils.circularSectorSurface(ROBOT_COMMUNICATION_RANGE, angle);
                return MathUtils.ringSurface(ROBOT_COMMUNICATION_RANGE) - invisibleArea;
            }
            if(DIMENSION == 3) return MathUtils.sphereVolume(ROBOT_COMMUNICATION_RANGE)/2;
        }
        return 0;
    }

    private double calculateAveragedDensity(Quadrotor unit){
        double densitySum = 0;
        for(Quadrotor neighbor : unit.getReducedNeighbors())densitySum+=neighbor.getDensityData().getOriginDensity();
        densitySum/=unit.getReducedNeighbors().size();
        return densitySum;
    }

    private double calculateDensity(Quadrotor unit){
        return unit.getReducedNeighbors().size()/calculateObservableArea(unit);
    }

    private double calculateOptimalDensity(){
        if(DIMENSION == 2){
            return 7 / MathUtils.ringSurface(ROBOT_DESIRED_DISTANCE);
        }
        if(DIMENSION == 3){
            return 19 / MathUtils.sphereVolume(ROBOT_DESIRED_DISTANCE);
        }
        return 0;
    }

    private void calculateDensityData(Quadrotor unit){
        unit.getDensityData().setOriginDensity(calculateDensity(unit));
        unit.getDensityData().setAveragedDensity(calculateAveragedDensity(unit));
        unit.getDensityData().setOptimalDensity(calculateOptimalDensity());
    }

    private double phi(double x){
        return (x == 0) ? 0 : (Math.pow(x, 3) / Math.abs(x));
    }

    private Vector3 densityForce(Quadrotor unit){
        Vector3 densityForce = new Vector3();

        for(Quadrotor neighbor: unit.getReducedNeighbors()){
            Vector3 increment = unit.getRelativeLocalization(neighbor);
            double deltaDensity = neighbor.getDensityData().getAveragedDensity()-neighbor.getDensityData().getOptimalDensity();
            increment.timesScalar(phi(deltaDensity));
            densityForce.plus(increment);
        }

        if(DIMENSION == 2)densityForce.setZ(0);
        if(unit.isBoundary())densityForce = new Vector3();  //  we do not apply density force to boundary robots
        return densityForce;
    }

    @Override
    public Vector3 loop(Quadrotor input) {
        calculateDensityData(input);

        Vector3 densityForce = densityForce(input);
        densityForce.timesScalar(param);

        return densityForce;
    }

}
