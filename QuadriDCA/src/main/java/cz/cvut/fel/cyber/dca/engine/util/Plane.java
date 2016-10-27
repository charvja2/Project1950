package cz.cvut.fel.cyber.dca.engine.util;

import static cz.cvut.fel.cyber.dca.engine.util.Vector3.dot;
import static cz.cvut.fel.cyber.dca.engine.util.Vector3.minus;

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

    public boolean pointLiesInPlane(Vector3 point){
        Vector4 eqParams = getEqutationParams();
        return ((eqParams.getA()*point.getX() + eqParams.getB()*point.getY() + eqParams.getC()*point.getZ() + eqParams.getD())==0);
    }

    public boolean pointLiesInTriangle(Vector3 point){
        Vector3 v0 = minus(getU(),getA());
        Vector3 v1 = minus(getV(),getA());
        Vector3 v2 = minus(point, getA());

        // Compute dot products
        double dot00 = dot(v0, v0);
        double dot01 = dot(v0, v1);
        double dot02 = dot(v0, v2);
        double dot11 = dot(v1, v1);
        double dot12 = dot(v1, v2);

        // Compute barycentric coordinates
        double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        // Check if point is in triangle
        return (u >= 0) && (v >= 0) && (u + v <= 1);
    }

}
