package cz.cvut.fel.cyber.dca.engine.util;

/**
 * Created by Jan on 14.10.2016.
 */
public class MathUtils {

    public static double ringSurface(double radius){
        return Math.PI*Math.pow(radius, 2);
    }

    public static double sphereVolume(double radius){
        return (4/3)*Math.PI*Math.pow(radius,3);
    }

    public static double sphereSurface(double radius){
        return 4*Math.PI*Math.pow(radius,2);
    }

    public static double circularSectorSurface(double radius, double angleRadians){
        return (Math.toDegrees(angleRadians)*ringSurface(radius))/360;
    }

}
