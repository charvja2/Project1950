package cz.cvut.fel.cyber.dca.engine.data;

/**
 * Created by Jan on 1. 12. 2015.
 */
public class ThicknessDeterminationData {

    private int sourceId;
    private int timeStamp;
    private int b;    // hop distance from boundary
    private int t;    // thickness
    private int h;    // circle center hop distance

    public ThicknessDeterminationData(int timeStamp, int sourceId) {
        this.timeStamp = timeStamp;
        this.sourceId = sourceId;
        this.b = 0;
        this.t = 0;
        this.h = 0;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public int getB() {
        return b;
    }

    public int getT() {
        return t;
    }

    public int getH() {
        return h;
    }

    public void setB(int b) {
        this.b = b;
    }

    public void setT(int t) {
        this.t = t;
    }

    public void setH(int h) {
        this.h = h;
    }

    @Override
    public String toString() {
        return "ThicknessDeterminationData{" +
                "sourceId = " + sourceId +
                ", timeStamp = " + timeStamp +
                ", boundary hop distance = " + b +
                ", thickness = " + t +
                ", center hop distance = " + h +
                '}';
    }
}
