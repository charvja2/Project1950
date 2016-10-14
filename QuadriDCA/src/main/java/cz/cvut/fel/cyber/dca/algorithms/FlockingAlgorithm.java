package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import cz.cvut.fel.cyber.dca.engine.core.Unit;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.*;
import static cz.cvut.fel.cyber.dca.engine.util.Vector3.*;
import static java.lang.Math.*;

/**
 * Created by Jan on 25. 10. 2015.
 */
public class FlockingAlgorithm implements Loopable<Quadrotor, Vector3>{

    private final double epsilon = 4;
    private final double a = 1;
    private final double b = 1;
    private final double c = abs(a-b)/sqrt(4*a*b);
    private final double h = 0.4;
    private final double param = 3;

    private double oNorm(Vector3 vector){
        return (1/epsilon)*(sqrt(1 + epsilon * pow(norm3(vector),2))-1);
    }

    private double oNorm(double z){
        return (1/epsilon)*(sqrt(1 + epsilon * pow(z,2))-1);
    }

    private double bumpFunction(double z){
        if((z>=0)&&(z<h))return 1;
        if((z>=h)&&(z<1))return 0.5*(1 + cos(PI*((z-h)/(1-h))));
        return 0;
    }

    private Vector3 mu(Vector3 q){
        double constant = (sqrt(1+epsilon*pow(norm3(q),2)));
        return new Vector3(q.getX()/constant,q.getY()/constant,q.getZ()/constant);
    }

    private double phiAlpha(double z){
        return bumpFunction(z/ oNorm(ROBOT_COMMUNICATION_RANGE))*phi(z - oNorm(ROBOT_DESIRED_DISTANCE));
    }

    private double phi(double z){
        return 0.5 *(((a+b)*((z+c)/sqrt(1+pow(z+c,2))))+(a-b));
    }

    private double v(Vector3 q){
        return bumpFunction(oNorm(q)/ oNorm(ROBOT_COMMUNICATION_RANGE));
    }

    @Override
    public Vector3 loop(Quadrotor unit) {
        if(unit==null)throw new NullPointerException();
        if(unit.getNeighbors().isEmpty())return new Vector3();

        Vector3 firstSum = new Vector3();
        Vector3 secondSum = new Vector3();

        // GRID
        for(Unit neighbor: unit.getNeighbors()){
            Vector3 increment = mu(unit.getRelativeLocalization(neighbor.getPosition()));
            increment.timesScalar(phiAlpha(oNorm(unit.getRelativeLocalization(neighbor.getPosition()))));
            firstSum.plus(increment);
        }
        // CONSENSUS
        for(Unit neighbor: unit.getNeighbors()){
            Vector3 increment = minus(neighbor.getLinearVelocity(),unit.getLinearVelocity());
            increment.timesScalar(v(unit.getRelativeLocalization(neighbor.getPosition())));
            secondSum.plus(increment);
        }
        secondSum.timesScalar(1/unit.getNeighbors().size());

        Vector3 acceleration = new Vector3();

        //firstSum.timesScalar(0.3);
        acceleration.plus(firstSum);
        //secondSum.timesScalar(1);
        acceleration.plus(secondSum);

        acceleration.timesScalar(param);

        if(DIMENSION == 2)acceleration.setZ(0.0);
        return acceleration;
    }
}
