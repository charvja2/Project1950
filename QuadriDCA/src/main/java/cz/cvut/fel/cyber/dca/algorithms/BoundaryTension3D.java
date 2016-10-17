package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.core.Swarm;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;

/**
 * Created by Jan on 15.10.2016.
 */
public class BoundaryTension3D implements Loopable<Quadrotor, Vector3>{

    private double wParam = 0.4;

    private Vector3 w(Vector3 boundaryForce){
        boundaryForce.timesScalar(wParam);
        return boundaryForce;
    }


    @Override
    public Vector3 loop(Quadrotor input) {
        if(Swarm.getMembers().size()<3)return new Vector3();

        Vector3 boundaryForce = new Vector3();

        input.getReducedNeighbors().stream().forEach(neighbor -> {
                    boundaryForce.plus(input.getRelativeLocalization(neighbor).newUnitVector());
        });

        return w(boundaryForce);
    }
}
