package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.*;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 13. 12. 2015.
 */
public class FollowPathAlgorithm implements Loopable<Quadrotor, Vector3> {

    private double param = 0.4;

    public double getParam() {
        return param;
    }

    public void setParam(double param) {
        this.param = param;
    }

    private Vector3 getFollowForce(Quadrotor unit){
        Path3D path = Swarm.getPath3DList().get(unit.getId());
        if(path.getPath().size()==0) return new Vector3();
        if(path.getPath().size()==unit.getLastCheckpoint())return new Vector3();

        Vector3 checkpointPosition = path.getPath().get(unit.getLastCheckpoint());
        if(checkpointPosition.distance(unit.getPosition())<0.5){
            unit.setLastCheckpoint(unit.getLastCheckpoint() + 1 );
        }

        Vector3 followForce = Vector3.minus(checkpointPosition, unit.getPosition());
        followForce.timesScalar(param);
        return followForce;
    }

    @Override
    public Vector3 loop(Quadrotor input) {
        return getFollowForce(input);
    }
}
