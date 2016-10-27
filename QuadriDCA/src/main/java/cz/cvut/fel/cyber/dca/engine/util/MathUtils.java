package cz.cvut.fel.cyber.dca.engine.util;

import static cz.cvut.fel.cyber.dca.engine.util.Vector3.*;
import Jama.Matrix;

import java.util.List;

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

    public static boolean segmentLineSegmentPlaneIntersection(Line line, Plane plane){
        if(!segmentPlaneIntersection(line,plane))return false;
        else{
            Vector3 intersectionPoint = getPointOfLinePlaneIntersection(line,plane);
            if(plane.pointLiesInTriangle(intersectionPoint)){
                return true;
            }
        }
        return false;
    }

    public static Vector3 getPointOfLinePlaneIntersection(Line line, Plane plane){
        if(linePlaneIntersection(line,plane)){
            Vector3 u = minus(line.getB(), line.getA());
            Vector3 n = plane.getNormalVector();

            double si = (dot(n, minus(plane.getA(),line.getA()) ))/(dot(n, minus(line.getB(),line.getA())));
            return Vector3.timesScalar(line.getA(),si);
        }
        return new Vector3();
    }

    public static boolean linePlaneIntersection(Line line, Plane plane){
        Vector3 u = minus(line.getB(), line.getA());
        Vector3 n = plane.getNormalVector();

        if(dot(n,u)==0){
            //parallel
            if(dot(n,minus(line.getA(),plane.getA()))==0){
                // coincidence
                return true;
            }else{
                // disjointness
                return false;
            }
        }

        return true;
    }

    public static boolean segmentPlaneIntersection(Line line, Plane plane){
        Vector3 u = minus(line.getB(), line.getA());
        Vector3 n = plane.getNormalVector();

        if(dot(n,u)==0){
            //parallel
            if(dot(n,minus(line.getA(),plane.getA()))==0){
                // coincidence
                return true;
            }else{
                // disjointness
                return false;
            }
        }

        double si = (dot(n, minus(plane.getA(),line.getA()) ))/(dot(n, minus(line.getB(),line.getA())));

        if((si>=0)&&(si<=1))return true;
        else return false;
    }

    public static double[] getAngleBetweenVectors(Vector3 a, Vector3 b){
        double alpha = Math.acos(dot(a,b)/(a.norm3()*b.norm3()));
        double[] results = {alpha, 2*Math.PI-alpha};
        return results;
    }

    public static boolean isPolyhedronClosed(List<Plane> planes){

        return false;
    }

}
