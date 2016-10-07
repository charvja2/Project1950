package cz.cvut.fel.cyber.dca.engine.core;

import cz.cvut.fel.cyber.dca.engine.util.Vector3;

/**
 * Created by Jan on 6. 12. 2015.
 */
public class DummyCheckpoint {

    private int id;
    private String vrepDummyName;
    private int objectHandle;
    private Vector3 position;

    public DummyCheckpoint(int id,String vrepDummyName) {
        this.id = id;
        this.vrepDummyName = vrepDummyName;
        objectHandle = 0;
        position = new Vector3();
    }

    public int getId() {
        return id;
    }

    public String getVrepDummyName() {
        return vrepDummyName;
    }

    public int getObjectHandle() {
        return objectHandle;
    }

    public void setObjectHandle(int objectHandle) {
        this.objectHandle = objectHandle;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }
}
