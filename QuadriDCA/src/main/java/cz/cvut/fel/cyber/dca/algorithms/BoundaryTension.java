package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.core.RobotGroup;
import cz.cvut.fel.cyber.dca.engine.core.Unit;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.DIMENSION;

/**
 * Created by Jan on 22. 11. 2015.
 */
public class BoundaryTension implements Loopable<Quadrotor, Vector3> {

    private double wParam = 0.4;

    private Vector3 w(Vector3 boundaryForce){
        boundaryForce.timesScalar(wParam);
        return boundaryForce;
    }

    @Override
    public Vector3 loop(Quadrotor input) {
        if(RobotGroup.getMembers().size()<3)return new Vector3();

        Map<Pair<Quadrotor,Quadrotor>, Double> emptySectors = BoundaryCommon.findEmptySectors(input);

        if(emptySectors.isEmpty())return new Vector3();


        Pair<Quadrotor,Quadrotor> neighbors = emptySectors.entrySet().stream()
                                                .max((a, b) -> (a.getValue()>b.getValue()) ? 1 : -1).get().getKey();


        Quadrotor firstNeighbor = neighbors.getKey();
        Quadrotor secondNeighbor = neighbors.getValue();

        Vector3 firstPos = input.getRelativeLocalization(firstNeighbor);
        Vector3 secondPos = input.getRelativeLocalization(secondNeighbor);

        firstPos.unitVector();
        secondPos.unitVector();

        Vector3 boundaryForce =  w(Vector3.plus(firstPos,secondPos));
        if(DIMENSION == 2)boundaryForce.setZ(0);

        return boundaryForce;
    }
}
