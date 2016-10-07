package cz.cvut.fel.cyber.dca.engine.core;

/**
 * Created by Jan on 16. 1. 2016.
 */
public class HeightLayer {

    private boolean allowedLayer;

    private double layerMinHeight;
    private double layerOptimalHeight;
    private double layerMaxHeight;

    public HeightLayer(double layerMinHeight, double layerMaxHeight) {
        this.layerMinHeight = layerMinHeight;
        this.layerMaxHeight = layerMaxHeight;
        this.layerOptimalHeight = (layerMaxHeight + layerMinHeight)/2;
        allowedLayer = true;
    }

    public HeightLayer(double layerMinHeight, double layerMaxHeight,boolean allowedLayer) {
        this.layerMinHeight = layerMinHeight;
        this.layerMaxHeight = layerMaxHeight;
        this.allowedLayer = allowedLayer;
        this.layerOptimalHeight = (layerMaxHeight + layerMinHeight)/2;
    }

    public double getLayerMinHeight() {
        return layerMinHeight;
    }

    public void setLayerMinHeight(double layerMinHeight) {
        this.layerMinHeight = layerMinHeight;
    }

    public double getLayerMaxHeight() {
        return layerMaxHeight;
    }

    public void setLayerMaxHeight(double layerMaxHeight) {
        this.layerMaxHeight = layerMaxHeight;
    }

    public boolean isAllowedLayer() {
        return allowedLayer;
    }

    public void setAllowedLayer(boolean allowedLayer) {
        this.allowedLayer = allowedLayer;
    }

    public double getLayerOptimalHeight() {
        return layerOptimalHeight;
    }

    public void setLayerOptimalHeight(double layerOptimalHeight) {
        this.layerOptimalHeight = layerOptimalHeight;
    }

    public boolean inRange(double z){
        return (z<getLayerMaxHeight())&&(z>getLayerMinHeight());
    }
}
