package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadracopter;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;

/**
 * Created by Jan on 10. 1. 2016.
 */
public class SimpleHeightSafetyControlAlgorithm implements Loopable<Pair<Quadracopter, Vector3>, Vector3> {

    @Override
    public Vector3 loop(Pair<Quadracopter, Vector3> input) {
        Quadracopter quadracopter = input.getKey();
        Vector3 velocity = input.getValue();

        if(quadracopter.getPosition().getZ()<quadracopter.getAllowedHeightRangeMin()
                || quadracopter.getPosition().getZ()>quadracopter.getAllowedHeightRangeMax()){

            velocity =  new Vector3(velocity.getX(),velocity.getY(),0);
        }
        return velocity;
    }
}
