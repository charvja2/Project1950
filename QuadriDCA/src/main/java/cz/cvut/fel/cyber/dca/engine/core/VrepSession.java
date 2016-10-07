/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.cyber.dca.engine.core;

import coppelia.remoteApi;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author Jan
 */
public class VrepSession {
    
    private int clientId;
    private String host;
    private final SimpleBooleanProperty connected;
    
    private remoteApi vrep;
    
    public VrepSession(String host) {
        clientId = 0;
        this.host = host;
        connected = new SimpleBooleanProperty(false);
        vrep = new remoteApi();
    }

    public VrepSession() {
        this.clientId = 0;
        this.host = "127.0.0.1";
        this.connected = new SimpleBooleanProperty(false);
        this.vrep = new remoteApi();
    }

    public boolean connect(){
            vrep.simxFinish(-1);
            vrep = new remoteApi();

            clientId = vrep.simxStart("127.0.0.1", 19999, true, true, 5000, 5);
            if (clientId == -1) return false;
            else {
                connected.set(true);
                return true;
            }

        }

    public void disconnect(){
        if(connected.get())vrep.simxFinish(clientId);
    }

    public int getClientId(){
        return clientId;
    }

    public boolean getConnected(){
        return connected.get();
    }

    public SimpleBooleanProperty getConnectedProperty(){
        return connected;
    }

    public remoteApi getVrep() {
        return vrep;
    }
}
