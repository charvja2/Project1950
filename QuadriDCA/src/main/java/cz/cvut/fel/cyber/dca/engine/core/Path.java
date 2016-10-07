package cz.cvut.fel.cyber.dca.engine.core;

import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.remoteApi;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Jan on 6. 12. 2015.
 */
public class Path {

    private int index;

    private List<DummyCheckpoint> checkpointList;

    public Path() {
        this.checkpointList = new ArrayList<>();
    }

    public Path(int index, int count) {
        this.checkpointList = new ArrayList<>();
        this.index = index;
        initCheckpoint(index, count);
    }

    public int getIndex() {
        return index;
    }

    private void initCheckpoint(int index, int count){
        if(count==0)return;
        for(int i = 0; i < count ; i++){
            checkpointList.add(new DummyCheckpoint(i, Integer.toString(index) + "Dummy" + ((i==0)?"":Integer.toString(i-1))));
        }
    }

    public List<DummyCheckpoint> getCheckpointList() {
        return checkpointList;
    }

    public void initVrep(VrepSession session){
        checkpointList.stream().forEach(dummyCheckpoint -> {
            IntW handle = new IntW(0);
            int errorCode = session.getVrep().simxGetObjectHandle(session.getClientId(), dummyCheckpoint.getVrepDummyName(), handle, remoteApi.simx_opmode_oneshot_wait);
            if (errorCode == remoteApi.simx_return_ok) {
                dummyCheckpoint.setObjectHandle(handle.getValue());
            }System.out.println("Failed to get checkpoint handle " + errorCode);

            FloatWA position = new FloatWA(3);
            errorCode = session.getVrep().simxGetObjectPosition(session.getClientId(), dummyCheckpoint.getObjectHandle(), -1, position, remoteApi.simx_opmode_oneshot_wait);
            if (errorCode == remoteApi.simx_return_ok) {
                dummyCheckpoint.setPosition(new Vector3(position));
            }System.out.println("Failed to get checkpoint position" + errorCode);
        });
    }

}
