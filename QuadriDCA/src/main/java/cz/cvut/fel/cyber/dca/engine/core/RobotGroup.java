/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.cyber.dca.engine.core;

import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.remoteApi;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.*;

/**
 *
 * @author Jan
 */
public class RobotGroup{

    private static final Logger LOGGER = Logger.getLogger(RobotGroup.class.getName());

    private static List<Quadracopter> members = new ArrayList<>();

    private static List<Path> pathList = new ArrayList<>();

    public static HeightLayerMapper getLayerMapper() {
        return layerMapper;
    }

    private static HeightLayerMapper layerMapper = new HeightLayerMapper();

    private static HeightProfile heightProfile = new HeightProfile();

    private static void initHeightProfile(){
        heightProfile.getLayers().add(new HeightLayer(0,1)); //landing layer
        heightProfile.getLayers().add(new HeightLayer(1,3));
        heightProfile.getLayers().add(new HeightLayer(4,6));
        heightProfile.getLayers().add(new HeightLayer(6,8));

        layerMapper.update(members,heightProfile);
    }

    public static void updateProfile(){
        layerMapper.update(members,heightProfile);
    }

    public static List<Path> getPathList() {
        return pathList;
    }

    public static List<Quadracopter> getMembers() {
        return members;
    }

    public static List<Quadracopter> getLeaders(){
        return members.stream().filter(member -> member.isLeader()).collect(Collectors.toList());
    }

    public static void initMembers(){
        members = new ArrayList<>();
        if(LEADER_COUNT!=0) members.add(new Quadracopter("Quadricopter", "Quadricopter_target", true));
        else members.add(new Quadracopter("Quadricopter", "Quadricopter_target", false));
        for(int i = 0; i < ROBOT_COUNT-1 ;i++){
            if (LEADER_COUNT-1 > i)members.add(new Quadracopter("Quadricopter#" + Integer.toString(i), "Quadricopter_target#" + Integer.toString(i), true));
            else members.add(new Quadracopter("Quadricopter#" + Integer.toString(i), "Quadricopter_target#" + Integer.toString(i), false));
        }
        initHeightProfile();
    }

    public static void initVrepMembers(VrepSession session){
        RobotGroup.getMembers().stream().forEach(unit -> {
            int clientID = session.getClientId();
            remoteApi vrep = session.getVrep();

            IntW quadriHandle = new IntW(0);
            int errorCode = vrep.simxGetObjectHandle(clientID, unit.getVrepUnitName(), quadriHandle, remoteApi.simx_opmode_oneshot_wait);
            if (errorCode == remoteApi.simx_return_ok) {
                unit.setVrepObjectHandle(quadriHandle.getValue());
            } else {
                LOGGER.log(Level.WARNING, "Failed to receive object handle for quadricopter. Error code:  " + errorCode );
            }
            errorCode = vrep.simxGetObjectHandle(clientID, unit.getVrepTargetName(), quadriHandle, remoteApi.simx_opmode_oneshot_wait);
            if (errorCode == remoteApi.simx_return_ok) {
                unit.setVrepTargetHandle(quadriHandle.getValue());
            } else {
                LOGGER.log(Level.WARNING, "Failed to receive object handle for quadricopter target. Error code:  " + errorCode );
            }
            FloatWA pos = new FloatWA(3);
            errorCode = vrep.simxGetObjectPosition(clientID, unit.getVrepTargetHandle(), -1,pos, remoteApi.simx_opmode_oneshot_wait);
            if (errorCode == remoteApi.simx_return_ok) {
                unit.setPosition(new Vector3(pos.getArray()[0], pos.getArray()[1], pos.getArray()[2]));
            } else {
                LOGGER.log(Level.WARNING, "Failed to receive quadricopter position. Error code:  " + errorCode );
            }
            FloatWA linearVelocity = new FloatWA(3);
            FloatWA angularVelocity = new FloatWA(3);
            errorCode = vrep.simxGetObjectVelocity(clientID, unit.getVrepObjectHandle(), linearVelocity,angularVelocity, remoteApi.simx_opmode_oneshot_wait);
            if (errorCode == remoteApi.simx_return_ok) {
                unit.setLinearVelocity(new Vector3(linearVelocity.getArray()[0], linearVelocity.getArray()[1], linearVelocity.getArray()[2]));
                unit.setAngularVelocity(new Vector3(angularVelocity.getArray()[0], angularVelocity.getArray()[1], angularVelocity.getArray()[2]));
            } else {
                LOGGER.log(Level.WARNING, "Failed to receive object handle for quadricopter velocity. Error code:  " + errorCode );
            }
        });
    }

    public static void launchDownloadBuffer(VrepSession session){
        RobotGroup.getMembers().stream().forEach(unit -> {
            int clientID = session.getClientId();
            remoteApi vrep = session.getVrep();

            FloatWA pos = new FloatWA(3);
            int errorCode = vrep.simxGetObjectPosition(clientID, unit.getVrepObjectHandle(), -1,pos, remoteApi.simx_opmode_streaming);
            if (errorCode == remoteApi.simx_return_ok) {
                unit.setPosition(new Vector3(pos.getArray()[0], pos.getArray()[1], pos.getArray()[2]));
            } else {
                LOGGER.log(Level.WARNING, "Failed to receive quadricopter position. Error code:  " + errorCode );
            }
            errorCode = vrep.simxGetObjectPosition(clientID, unit.getVrepTargetHandle(), -1,pos, remoteApi.simx_opmode_streaming);
            if (errorCode == remoteApi.simx_return_ok) {
                unit.setTargetPosition(new Vector3(pos.getArray()[0], pos.getArray()[1], pos.getArray()[2]));
            } else {
                LOGGER.log(Level.WARNING, "Failed to receive quadricopter position. Error code:  " + errorCode );
            }
            FloatWA linearVelocity = new FloatWA(3);
            FloatWA angularVelocity = new FloatWA(3);
            errorCode = vrep.simxGetObjectVelocity(clientID, unit.getVrepObjectHandle(), linearVelocity,angularVelocity, remoteApi.simx_opmode_streaming);
            if (errorCode == remoteApi.simx_return_ok) {
                unit.setLinearVelocity(new Vector3(linearVelocity.getArray()[0], linearVelocity.getArray()[1], linearVelocity.getArray()[2]));
                unit.setAngularVelocity(new Vector3(angularVelocity.getArray()[0], angularVelocity.getArray()[1], angularVelocity.getArray()[2]));
            } else {
                LOGGER.log(Level.WARNING, "Failed to receive quadricopter velocity. Error code:  " + errorCode );
            }
        });
    }

    public static void downloadMembers(VrepSession session){
        RobotGroup.getMembers().stream().forEach(unit -> {
            int clientID = session.getClientId();
            remoteApi vrep = session.getVrep();

            FloatWA pos = new FloatWA(3);
            int errorCode = vrep.simxGetObjectPosition(clientID, unit.getVrepObjectHandle(), -1,pos, remoteApi.simx_opmode_buffer);
            if (errorCode == remoteApi.simx_return_ok) {
                unit.setPosition(new Vector3(pos.getArray()[0], pos.getArray()[1], pos.getArray()[2]));
            } else {
                LOGGER.log(Level.WARNING, "Failed to receive quadricopter position. Error code:  " + errorCode);
            }
            errorCode = vrep.simxGetObjectPosition(clientID, unit.getVrepTargetHandle(), -1,pos, remoteApi.simx_opmode_buffer);
            if (errorCode == remoteApi.simx_return_ok) {
                unit.setTargetPosition(new Vector3(pos.getArray()[0], pos.getArray()[1], pos.getArray()[2]));
            } else {
                LOGGER.log(Level.WARNING, "Failed to receive quadricopter position. Error code:  " + errorCode );
            }

            FloatWA linearVelocity = new FloatWA(3);
            FloatWA angularVelocity = new FloatWA(3);
            errorCode = vrep.simxGetObjectVelocity(clientID, unit.getVrepObjectHandle(), linearVelocity,angularVelocity, remoteApi.simx_opmode_buffer);
            if (errorCode == remoteApi.simx_return_ok) {
                unit.setLinearVelocity(new Vector3(linearVelocity.getArray()[0], linearVelocity.getArray()[1], linearVelocity.getArray()[2]));
                if(unit.getLinearVelocity().norm3()<10E-3)unit.setLinearVelocity(new Vector3());
                unit.setAngularVelocity(new Vector3(angularVelocity.getArray()[0], angularVelocity.getArray()[1], angularVelocity.getArray()[2]));
            } else {
                LOGGER.log(Level.WARNING, "Failed to receive quadricopter velocity. Error code:  " + errorCode);
            }
        });
    }

    public static void uploadVrepMembers(VrepSession session){
        RobotGroup.getMembers().stream().forEach(unit -> {
            int clientID = session.getClientId();
            remoteApi vrep = session.getVrep();

            FloatWA targetPosition = new FloatWA(3);
            targetPosition.getArray()[0]=(float)unit.getTargetPosition().getX();
            targetPosition.getArray()[1]=(float)unit.getTargetPosition().getY();
            targetPosition.getArray()[2]=(float)unit.getTargetPosition().getZ();
            if (unit.getTargetPosition().isVectorNull())return;
            int errorCode = vrep.simxSetObjectPosition(clientID, unit.getVrepTargetHandle(), -1, targetPosition
                                                                                        , remoteApi.simx_opmode_oneshot);
            if (errorCode == remoteApi.simx_return_ok) {

            } else {
                LOGGER.log(Level.WARNING, "Failed to set quadricopter target position. Error code:  " + errorCode );
            }
        });
    }

    public static void printMembers(){
        System.out.println("--------------------------------------------------");
        members.stream().forEach(robot -> System.out.println(robot.toString()));
    }

    public static boolean[][] getNetConnectionGraph(){
        boolean[][] graph = new boolean[members.size()][members.size()];
        for(int x = 0; x < graph.length; x++){
            for(int y = 0; y < graph.length; y++){
                graph[x][y]=false;
            }
        }
        members.forEach(robot -> {
            robot.getNeighbors().forEach(neighbor->{
                graph[robot.getId()][neighbor.getId()]=true;
                graph[neighbor.getId()][robot.getId()]=true;
            });});

        return graph;
    }

    public static boolean withinCommunicationRange(Unit a, Unit b){
        return a.getPosition().distance(b.getPosition()) < ROBOT_COMMUNICATION_RANGE;
    }

    public static HeightProfile getGroupHeightProfile(){
        return heightProfile;
    }

    public static Void loop(Long input) {
        members.stream().forEach(unit -> unit.loop(input));
        return null;
    }

}
