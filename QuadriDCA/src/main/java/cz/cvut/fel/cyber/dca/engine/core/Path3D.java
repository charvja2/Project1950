package cz.cvut.fel.cyber.dca.engine.core;

import cz.cvut.fel.cyber.dca.engine.util.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Created by Jan on 07.10.2016.
 */
public class Path3D {

    private int index;
    private List<Vector3> path;

    public Path3D(int index, List<Vector3> path) {
        this.index = index;
        this.path = path;
    }

    public int getIndex() {
        return index;
    }

    public List<Vector3> getPath() {
        return path;
    }

    private List<Vector3> generateRandomPath(int N, int range){
        List<Vector3> path = new ArrayList<>();
        IntStream.range(0, N).forEach( number -> path.add(new Vector3(
            ThreadLocalRandom.current().nextDouble(-range, +range),
            ThreadLocalRandom.current().nextDouble(-range, +range),
            ThreadLocalRandom.current().nextDouble(1, +range)
        )));
        return path;
    }

    public static List<Vector3> getCirclePath(int N, Vector3 center, double radius){
        List<Vector3> path = new ArrayList<>();

        double phi = 0;
        for(int i = 0; i < N; i++){
            path.add(new Vector3(center.getX()+radius*Math.cos(phi),center.getY()+radius*Math.sin(phi),center.getZ()));
            phi += 2*Math.PI/N;
        }

        return path;
    }

    public static List<Vector3> getLinePath(int N, Vector3 a, Vector3 b){
        List<Vector3> path = new ArrayList<>();

        Vector3 ab = Vector3.minus(b, a);
        ab.timesScalar((double)1/N);
        for(int i = 0; i < N; i++){
            path.add(Vector3.plus(a, Vector3.timesScalar(ab, (i+1))));
        }

        return path;
    }

}
