package cz.cvut.fel.cyber.dca.engine.util;

/**
 * Created by Jan on 24.10.2016.
 */
public class Plane {

    private Vector3 a;
    private Vector3 u;
    private Vector3 v;

    public Plane(Vector3 a, Vector3 u, Vector3 v) {
        this.a = a;
        this.u = u;
        this.v = v;
    }

    public Vector3 getA() {
        return a;
    }

    public Vector3 getU() {
        return u;
    }

    public Vector3 getV() {
        return v;
    }

    public Vector3 getNormalVector(){
        return Vector3.cross(u,v);
    }

    public Vector4 getEqutationParams(){
        Vector3 n = getNormalVector();
        double d =  -a.dot(n);
        return new Vector4(n,d);
    }

}
