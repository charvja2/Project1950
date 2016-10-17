package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.gui.ServiceLogger;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;

import java.util.Map;

/**
 * Created by Jan on 15.10.2016.
 */
public class BoundaryDetection3D implements Loopable<Quadrotor, Boolean> {

    private boolean isBoundaryX(Quadrotor input){
        Map<Pair<Quadrotor, Quadrotor>, Double> emptySectors = BoundaryCommon.findEmptySectorsX(input);
        if(input.getNeighbors().isEmpty())return true;
        return !emptySectors.isEmpty();
    }

    private boolean isBoundaryY(Quadrotor input){
        Map<Pair<Quadrotor, Quadrotor>, Double> emptySectors = BoundaryCommon.findEmptySectorsY(input);
        if(input.getNeighbors().isEmpty())return true;
        return !emptySectors.isEmpty();
    }

    private boolean isBoundaryZ(Quadrotor input){
        Map<Pair<Quadrotor, Quadrotor>, Double> emptySectors = BoundaryCommon.findEmptySectorsZ(input);
        if(input.getNeighbors().isEmpty())return true;
        return !emptySectors.isEmpty();
    }

    @Override
    public Boolean loop(Quadrotor input) {
        input.getBoundaryVector().setX(isBoundaryX(input));
        input.getBoundaryVector().setY(isBoundaryY(input));
        input.getBoundaryVector().setZ(isBoundaryZ(input));

        return input.getBoundaryVector().and();
    }
}
