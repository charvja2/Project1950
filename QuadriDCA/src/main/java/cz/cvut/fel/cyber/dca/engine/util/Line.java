package cz.cvut.fel.cyber.dca.engine.util;

/**
 * Created by Jan on 24.10.2016.
 */
public class Line {

    private Vector3 a;
    private Vector3 b;

    public Line(Vector3 a, Vector3 b) {
        this.a = a;
        this.b = b;
    }

    public Line(Vector3 b) {
        this.a = new Vector3();
        this.b = b;
    }

    public Vector3 getA() {
        return a;
    }

    public Vector3 getB() {
        return b;
    }
}
