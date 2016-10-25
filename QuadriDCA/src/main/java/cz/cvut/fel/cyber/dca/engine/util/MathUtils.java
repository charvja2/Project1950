package cz.cvut.fel.cyber.dca.engine.util;

import static cz.cvut.fel.cyber.dca.engine.util.Vector3.*;

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

    public static boolean segmentPlaneIntersection(Line line, Plane plane){
        Vector3 u = minus(line.getB(), line.getA());

        Vector3 n = plane.getNormalVector();

        if(dot(n,u)==0){
            //parallel
            return false;
        }

        double si = (dot(n, minus(plane.getA(),line.getA()) ))/(Vector3.dot(n, minus(line.getB(),line.getA())));

        if((si>=0)&&(si<=1))return true;
        else return false;
    }

}
