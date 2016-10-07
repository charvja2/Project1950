package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadracopter;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;

/**
 * Created by Jan on 12. 12. 2015.
 */
public class DensityAlgorithm implements Loopable<Quadracopter, Vector3> {

    private double param = 0.5;
    private double optimalDensity = 7/(Math.PI*Math.pow(Experiment.ROBOT_DESIRED_DISTANCE,3));

    private void calculateDensityData(Quadracopter unit){
        unit.getDensityData().setOriginDensity(density(unit));

        double densitySum = 0;
        for(Quadracopter neighbor : unit.getNeighbors())densitySum+=neighbor.getDensityData().getOriginDensity();
        densitySum/=unit.getNeighbors().size();
        unit.getDensityData().setAveragedDesnity(densitySum);

        if(unit.isBoundary()){
        if(unit.isConvexBoundary())unit.getDensityData().setOptimalDensity(3.5/(Math.PI*Math.pow(Experiment.ROBOT_DESIRED_DISTANCE,3)));
            else unit.getDensityData().setOptimalDensity(optimalDensity);}
        else unit.getDensityData().setOptimalDensity(optimalDensity);
    }

    private double phi(double x){
        if(x == 0 ) System.out.println("Density NAN source!///////////////////////////////////////////////////////////");
        return Math.pow(x,3)/Math.abs(x);
    }

    private double density(Quadracopter unit){
        return unit.getVisibleNeighbors().size()/(Math.PI*Math.pow(Experiment.ROBOT_COMMUNICATION_RANGE,2));
    }

    private Vector3 denstityForce(Quadracopter unit){
        Vector3 densityForce = new Vector3();
        for(Quadracopter neighbor: unit.getVisibleNeighbors()){
            Vector3 increment = unit.getRelativeLocalization(neighbor);
            double konst = phi(neighbor.getDensityData().getAveragedDesnity()-neighbor.getDensityData().getOptimalDensity());
            increment.timesScalar(konst);
            if(!Double.isNaN(increment.getX()))
                    densityForce.plus(increment);
        }
        densityForce.setZ(0);
        if(unit.isBoundary())densityForce = new Vector3();
        return densityForce;
    }

    @Override
    public Vector3 loop(Quadracopter input) {
        calculateDensityData(input);

        Vector3 densityFc = denstityForce(input);
        densityFc.timesScalar(param);

        return densityFc;
    }

}
