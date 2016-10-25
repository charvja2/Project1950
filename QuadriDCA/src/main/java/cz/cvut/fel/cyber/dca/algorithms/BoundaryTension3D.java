package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.core.Swarm;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;

import java.util.Map;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.DIMENSION;
import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.ROBOT_DESIRED_DISTANCE;

/**
 * Created by Jan on 15.10.2016.
 */
public class BoundaryTension3D implements Loopable<Quadrotor, Vector3>{

    private double wParam = 0.2;

    public double getwParam() {
        return wParam;
    }

    public void setwParam(double wParam) {
        this.wParam = wParam;
    }

    private Vector3 w(Vector3 boundaryForce){
        boundaryForce.timesScalar(wParam);
        return boundaryForce;
    }

    private Vector3 getBoundaryForce(Quadrotor input, Map<Pair<Quadrotor,Quadrotor>, Double> emptySectors){
        if(emptySectors.isEmpty())return new Vector3();

        Pair<Quadrotor,Quadrotor> neighbors = emptySectors.entrySet().stream()
                .max((a, b) -> (a.getValue()>b.getValue()) ? 1 : -1).get().getKey();

        Quadrotor firstNeighbor = neighbors.getKey();
        Quadrotor secondNeighbor = neighbors.getValue();

        Vector3 firstPos = input.getRelativeLocalization(firstNeighbor);
        Vector3 secondPos = input.getRelativeLocalization(secondNeighbor);

        if(firstPos.norm3()<ROBOT_DESIRED_DISTANCE)firstPos.timesScalar(-1);
        if(secondPos.norm3()<ROBOT_DESIRED_DISTANCE)secondPos.timesScalar(-1);

        firstPos.unitVector();
        secondPos.unitVector();

        Vector3 boundaryForce =  w(Vector3.plus(firstPos,secondPos));

        return boundaryForce;
    }


    @Override
    public Vector3 loop(Quadrotor input) {
        if(Swarm.getMembers().size()<3)return new Vector3();

        Vector3 boundaryForce = new Vector3();

        input.getReducedNeighbors().stream().forEach(neighbor -> {
                    boundaryForce.plus(input.getRelativeLocalization(neighbor).newUnitVector());
        });

/*
        if(input.getBoundaryVector().isX()) {
            Vector3 boundaryForceX = getBoundaryForce(input, BoundaryCommon.findEmptySectorsX(input));
            //boundaryForceX.setX(0);
            boundaryForce.plus(boundaryForceX);
        }
        if(input.getBoundaryVector().isY()) {
            Vector3 boundaryForceY = getBoundaryForce(input, BoundaryCommon.findEmptySectorsY(input));
            //boundaryForceY.setY(0);
            boundaryForce.plus(boundaryForceY);
        }
        if(input.getBoundaryVector().isZ()) {
            Vector3 boundaryForceZ = getBoundaryForce(input, BoundaryCommon.findEmptySectorsZ(input));
            //boundaryForceZ.setZ(0);
            boundaryForce.plus(boundaryForceZ);
        }
*/
        return w(boundaryForce);
    }


}
