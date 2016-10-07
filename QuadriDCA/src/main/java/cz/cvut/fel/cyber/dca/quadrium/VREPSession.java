package cz.cvut.fel.cyber.dca.quadrium;

import coppelia.IntW;
import coppelia.remoteApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jan on 18. 10. 2015.
 */
public class VREPSession {

    private int clientID;
    private final String host;
    private final remoteApi vrep;
    private final Map<String, IntW> handles;

    public VREPSession(String host) {
        this.host = host;
        this.vrep = new remoteApi();
        this.handles = new HashMap<>();
    }

    public boolean connect(){
        clientID = VREPHelper.connect(vrep, host);
        if(clientID == -1)return false;
        else return true;
    }

    public int getHandle(String objectName, IntW handle){
        int ret = vrep.simxGetObjectHandle(clientID, objectName, handle, remoteApi.simx_opmode_oneshot_wait);
        if(ret == 0){handles.put(objectName, handle);}
        return ret;
    }

    public void disconnect(){
        vrep.simxFinish(clientID);
        handles.clear();
    }

    public Map<String, IntW> getHandles() {
        return handles;
    }

    public remoteApi getVrep() {
        return vrep;
    }

    public int getClientID() {
        return clientID;
    }

    public String getHost() {
        return host;
    }
}
