package cz.cvut.fel.cyber.dca.engine.data;

/**
 * Created by Jan on 12. 12. 2015.
 */
public class DensityData {

    private volatile double originDensity;
    private volatile double averagedDensity;
    private volatile double optimalDensity;

    public DensityData() {
        optimalDensity = 0;
        averagedDensity = 0;
        originDensity = 0;
    }

    public void setOriginDensity(double originDensity) {
        this.originDensity = originDensity;
    }

    public void setAveragedDensity(double averagedDensity) {
        this.averagedDensity = averagedDensity;
    }

    public void setOptimalDensity(double optimalDensity) {
        this.optimalDensity = optimalDensity;
    }

    public double getOriginDensity() {
        return originDensity;
    }

    public double getAveragedDensity() {
        return averagedDensity;
    }

    public double getOptimalDensity() {
        return optimalDensity;
    }

    @Override
    public String toString() {
        return "DensityData{" +
                "originDensity = " + originDensity +
                ", averagedDensity = " + averagedDensity +
                ", optimalDensity = " + optimalDensity +
                '}';
    }
}
