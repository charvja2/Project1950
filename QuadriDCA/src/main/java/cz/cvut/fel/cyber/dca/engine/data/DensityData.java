package cz.cvut.fel.cyber.dca.engine.data;

/**
 * Created by Jan on 12. 12. 2015.
 */
public class DensityData {

    private volatile double originDensity;
    private volatile double averagedDesnity;
    private volatile double optimalDensity;

    public DensityData() {
        optimalDensity = 0;
        averagedDesnity = 0;
        originDensity = 0;
    }

    public void setOriginDensity(double originDensity) {
        this.originDensity = originDensity;
    }

    public void setAveragedDesnity(double averagedDesnity) {
        this.averagedDesnity = averagedDesnity;
    }

    public void setOptimalDensity(double optimalDensity) {
        this.optimalDensity = optimalDensity;
    }

    public double getOriginDensity() {
        return originDensity;
    }

    public double getAveragedDesnity() {
        return averagedDesnity;
    }

    public double getOptimalDensity() {
        return optimalDensity;
    }

    @Override
    public String toString() {
        return "DensityData{" +
                "originDensity = " + originDensity +
                ", averagedDesnity = " + averagedDesnity +
                ", optimalDensity = " + optimalDensity +
                '}';
    }
}
