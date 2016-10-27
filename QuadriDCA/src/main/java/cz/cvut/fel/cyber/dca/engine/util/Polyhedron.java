package cz.cvut.fel.cyber.dca.engine.util;

import java.util.List;

import static cz.cvut.fel.cyber.dca.engine.util.MathUtils.*;

/**
 * Created by Jan on 27.10.2016.
 */
public class Polyhedron {

    private List<Plane> faces;

    public Polyhedron(List<Plane> faces) {
        this.faces = faces;
    }

    public boolean isClosed(){


        return false;
    }
/*
    public boolean isPointInside(Vector3 point){
        int intersectionCounter = 0;
        Line ray = new Line(point, new Vector3(1,1,1));
        for(Plane face : faces){
            if(linePlaneIntersection(ray,face)){
                pointInsideTringle(face,getPointOfLinePlaneIntersection(ray,face));
            }
        }
        if((intersectionCounter%2)==1)return true;
        return false;
    }
    */
}
