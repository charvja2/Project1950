package cz.cvut.fel.cyber.dca.engine.util;

/**
 * Created by Jan on 17.10.2016.
 */
public class LogicalVector {

    private boolean x;
    private boolean y;
    private boolean z;

    public LogicalVector(boolean x, boolean y, boolean z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LogicalVector(boolean val) {
        this.x = val;
        this.y = val;
        this.z = val;
    }

    public LogicalVector() {
        this.x = false;
        this.y = false;
        this.z = false;
    }

    public boolean isX() {
        return x;
    }

    public void setX(boolean x) {
        this.x = x;
    }

    public boolean isY() {
        return y;
    }

    public void setY(boolean y) {
        this.y = y;
    }

    public boolean isZ() {
        return z;
    }

    public void setZ(boolean z) {
        this.z = z;
    }

    public boolean or(){
        return x && y && z;
    }

    public boolean and(){
        return x || y || z;
    }

    @Override
    public String toString() {
        return "[" +
                " " + x +
                " " + y +
                " " + z +
                ']';
    }
}
