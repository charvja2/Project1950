package cz.cvut.fel.cyber.dca.engine.core;

import cz.cvut.fel.cyber.dca.engine.util.Vector3;

import java.util.List;

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



}
