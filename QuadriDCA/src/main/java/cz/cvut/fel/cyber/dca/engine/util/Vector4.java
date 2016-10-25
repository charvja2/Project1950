package cz.cvut.fel.cyber.dca.engine.util;

/**
 * Created by Jan on 24.10.2016.
 */
public class Vector4 {

    private double a;
    private double b;
    private double c;
    private double d;

    public Vector4(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public Vector4(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = 0;
    }

    public Vector4(Vector3 a, double d) {
        this.a = a.getX();
        this.b = a.getY();
        this.c = a.getZ();
        this.d = d;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

    public Vector3 toVector3(){
        return new Vector3(a,b,c);
    }


}
