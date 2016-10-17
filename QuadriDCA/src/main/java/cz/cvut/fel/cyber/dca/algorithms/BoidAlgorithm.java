package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.*;
import static cz.cvut.fel.cyber.dca.engine.util.Vector3.*;

/**
 * Created by Jan on 1. 11. 2015.
 */
public class BoidAlgorithm implements Loopable<Quadrotor, Vector3> {

    private double param = 0.2;

    private double S = 0.8;
    private double K = 1;
    private double M = 0.7;

    private Vector3 separation(Quadrotor unit){
        Vector3 separationForce = new Vector3();
        //unit.getNeighborRelLocMapper().values().forEach(separationForce::plus);

        unit.getNeighborRelLocMapper().entrySet().forEach(entry -> {
            if(!unit.getReducedNeighbors().contains(entry.getKey()))return;

            Vector3 pos = new Vector3(entry.getValue());
            pos.timesScalar(ROBOT_DESIRED_DISTANCE / unit.getDistance(entry.getKey()));
            //additional collision avoidance
            if(unit.getDistance(entry.getKey())<ROBOT_SAFETY_ZONE)
                pos.timesScalar(Math.pow(ROBOT_SAFETY_ZONE / unit.getDistance(entry.getKey()),8));

            separationForce.plus(pos);
        });
        separationForce.timesScalar(-1);
        return separationForce;
    }

    private Vector3 cohesion(Quadrotor unit){
        Vector3 cohesionForce = new Vector3();
        Vector3 massCentre = new Vector3();

        unit.getNeighborRelLocMapper().entrySet().forEach(entry -> {
            if(!unit.getReducedNeighbors().contains(entry.getKey()))return;
            Vector3 pos = new Vector3(entry.getValue());
            if(entry.getKey().isLeader()&&(unit.getDistance(entry.getKey())>ROBOT_DESIRED_DISTANCE))
                pos.timesScalar(unit.getDistance(entry.getKey())/ROBOT_DESIRED_DISTANCE);
/*
            if(THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED){
                if(entry.getKey().getThicknessDeterminationData().getT()<unit.getThicknessDeterminationData().getT())
                    pos.timesScalar((unit.getThicknessDeterminationData().getT()+1)/(entry.getKey().getThicknessDeterminationData().getT()+1));
            }
*/
            if(unit.isTailRobot()&&unit.getReducedNeighbors().size()==1)
                pos.timesScalar(unit.getDistance(entry.getKey())/ROBOT_DESIRED_DISTANCE);

            pos.timesScalar(unit.getDistance(entry.getKey())/ROBOT_DESIRED_DISTANCE);
            massCentre.plus(pos);
        });
        //massCentre.timesScalar(-1);
        cohesionForce = massCentre;

        return cohesionForce;
    }

    private Vector3 alignment(Quadrotor unit){
        Vector3 steeringForce = new Vector3();
        Vector3 mass = new Vector3();

        unit.getReducedNeighbors().stream().map(neighbor -> neighbor.getLinearVelocity()).forEach(mass::plus);

        if(!unit.getReducedNeighbors().isEmpty())mass.timesScalar(1/unit.getNeighbors().size());

        return mass;
    }

    @Override
    public Vector3 loop(Quadrotor input) {
        Vector3 separationForce = separation(input);
        Vector3 cohesionForce = cohesion(input);
        Vector3 matching = alignment(input);

        separationForce.timesScalar(S);
        cohesionForce.timesScalar(K);
        matching.timesScalar(M);

        Vector3 velocity = plus(separationForce, plus(cohesionForce, matching));

        velocity.plus(input.getLinearVelocity());
        velocity.timesScalar(param);
        if(DIMENSION==2)velocity.setZ(0);

        return velocity;
    }

}