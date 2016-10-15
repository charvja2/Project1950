package cz.cvut.fel.cyber.dca.engine.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 16. 1. 2016.
 */
public class HeightProfile {

    private List<HeightLayer> layers;

    private int defaultLayer = 2;

    public HeightProfile() {
        layers = new ArrayList<>();
    }

    public List<HeightLayer> getLayers() {
        return layers;
    }

    public HeightLayer getDefaultLayer(){
        return layers.get(defaultLayer);
    }

    public int getDefaultLayerIndex(){
        return defaultLayer;
    }

}
