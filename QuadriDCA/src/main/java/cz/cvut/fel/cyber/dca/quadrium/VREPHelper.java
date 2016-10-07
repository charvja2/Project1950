package cz.cvut.fel.cyber.dca.quadrium;

import coppelia.remoteApi;

/**
 * Created by Jan on 18. 10. 2015.
 */
public class VREPHelper {



    public static int connect(remoteApi api,String host){
        api.simxFinish(-1);
        return api.simxStart(host, 19999, true, true, 5000, 5);
    }
}
