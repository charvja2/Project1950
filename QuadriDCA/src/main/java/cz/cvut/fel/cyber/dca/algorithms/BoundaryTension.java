package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadracopter;
import cz.cvut.fel.cyber.dca.engine.core.RobotGroup;
import cz.cvut.fel.cyber.dca.engine.core.Unit;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;

import java.util.Set;
import java.util.stream.Collectors;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.*;

/**
 * Created by Jan on 22. 11. 2015.
 */
public class BoundaryTension implements Loopable<Quadracopter, Vector3> {

    private double wParam = -0.4;

    private Vector3 w(Vector3 boundaryForce){
        boundaryForce.timesScalar(wParam);
        return boundaryForce;
    }

    @Override
    public Vector3 loop(Quadracopter input) {
        if(RobotGroup.getMembers().size()<3)return new Vector3();

        Set<Quadracopter> boundaryRobots = input.getBoundaryNeighbors();
        if(boundaryRobots.isEmpty()||boundaryRobots.size()<3)return new Vector3();

        boundaryRobots = boundaryRobots.stream().sorted((a,b) -> {
            if (a.getPosition().distance(input.getPosition()) > b.getPosition().distance(input.getPosition()))return 1;
            else return -1;
        }).collect(Collectors.toSet());

        Unit firstNeighbor = (Unit)boundaryRobots.toArray()[0];
        Unit secondNeighbor = (Unit)boundaryRobots.toArray()[1];

        Vector3 firstPos = input.getRelativeLocalization(firstNeighbor);
        Vector3 secondPos = input.getRelativeLocalization(secondNeighbor);


        //if((firstPos.norm3()< ROBOT_DESIRED_DISTANCE)&&(secondPos.norm3()< ROBOT_DESIRED_DISTANCE))
        //    return new Vector3();
        /*
        if ((!((Quadracopter)firstNeighbor).isLeader())&&(!((Quadracopter)secondNeighbor).isLeader())) {
            if (firstPos.norm3() < ROBOT_DESIRED_DISTANCE) firstPos.timesScalar(-1);
            if (secondPos.norm3() < ROBOT_DESIRED_DISTANCE) secondPos.timesScalar(-1);
        }
*/

        //if (!((Quadracopter)firstNeighbor).isLeader())
            if (firstPos.norm3() < ROBOT_DESIRED_DISTANCE) firstPos.timesScalar(-1);
        //if (!((Quadracopter)secondNeighbor).isLeader())
            if (secondPos.norm3() < ROBOT_DESIRED_DISTANCE) secondPos.timesScalar(-1);


        firstPos.unitVector();
        secondPos.unitVector();

        Vector3 boundaryForce =  w(Vector3.plus(firstPos,secondPos));

        //boundaryForce.setZ(0);

        return boundaryForce;
    }
}
