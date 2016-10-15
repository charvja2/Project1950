package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import javafx.util.Pair;

import java.util.Map;

/**
 * Created by Jan on 28. 10. 2015.
 */
public class BoundaryDetection implements Loopable<Quadrotor, Boolean> {

    @Override
    public Boolean loop(Quadrotor input) {
        Map<Pair<Quadrotor, Quadrotor>, Double> emptySectors = BoundaryCommon.findEmptySectorsZ(input);
        if(input.getNeighbors().isEmpty())return true;
        return !emptySectors.isEmpty();
    }
}
