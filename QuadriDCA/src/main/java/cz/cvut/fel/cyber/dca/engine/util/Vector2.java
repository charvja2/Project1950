package cz.cvut.fel.cyber.dca.engine.util;

/**
 * Created by Jan on 3. 11. 2015.
 */
public class Vector2 {

    private double x;
    private double y;

    public Vector2() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2(double x,double y) {
        this.x = x;
        this.y = y;
    }

    public Vector3 toVector3(){
        return new Vector3(x,y,0);
    }

    public Vector3 toVector3(double z){
        return new Vector3(x,y,z);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double norm(){
        return Math.sqrt(x*x+y*y);
    }



}
