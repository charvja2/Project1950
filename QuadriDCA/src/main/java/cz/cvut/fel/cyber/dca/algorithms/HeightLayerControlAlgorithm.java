package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.*;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;

/**
 * Created by Jan on 16. 1. 2016.
 */
public class HeightLayerControlAlgorithm implements Loopable<Quadracopter,Vector3> {

    private double speed = 0.7;
    private double param = 0.1;

    @Override
    public Vector3 loop(Quadracopter input) {
        double height = input.getHeight();
        HeightLayer layer = input.getLayer();
        HeightProfile profile = RobotGroup.getGroupHeightProfile();

        Vector3 layerForce = new Vector3();

        if(layer.inRange(height)){
            double difference = layer.getLayerOptimalHeight()-height;
            if(Math.abs(difference)<0.05)return new Vector3();
            if(difference>0){
                layerForce.setZ(0.2*speed*difference);
            }else{
                layerForce.setZ(0.2*speed*difference);
            }
        }else{
            double difference = layer.getLayerOptimalHeight()-height;
            if(difference>0){
                layerForce.setZ(speed*difference);
            }else{
                layerForce.setZ(speed*difference);
            }
        }
        return layerForce;
    }

}
