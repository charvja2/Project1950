package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.*;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;

/**
 * Created by Jan on 16. 1. 2016.
 */
public class HeightLayerControlAlgorithm implements Loopable<Quadrotor,Vector3> {

    private double speed = 0.7;
    private double param = 5;

    private HeightLayer rule(Quadrotor unit){
        if(unit.isLeader()) return unit.getHeightProfile().getLayers().get(unit.getHeightProfile().getDefaultLayerIndex()+1);
        if(!unit.isBoundary())return unit.getHeightProfile().getLayers().get(unit.getHeightProfile().getDefaultLayerIndex()-1);
        else return unit.getHeightProfile().getDefaultLayer();
    }

    @Override
    public Vector3 loop(Quadrotor input) {
        double height = input.getHeight();
        HeightProfile profile = input.getHeightProfile();
        HeightLayer defaultLayer = rule(input);

        Vector3 layerForce = new Vector3();

        if(defaultLayer.inRange(height)){
            double difference = defaultLayer.getLayerOptimalHeight()-height;
            if(Math.abs(difference)<0.05)return new Vector3();
            else layerForce.setZ(0.7*speed*difference);
        }else{
            double difference = defaultLayer.getLayerOptimalHeight()-height;
            layerForce.setZ(speed*difference);
        }

        layerForce.timesScalar(param);

        return layerForce;
    }

}
