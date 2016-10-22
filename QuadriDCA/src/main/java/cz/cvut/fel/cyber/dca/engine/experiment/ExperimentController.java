package cz.cvut.fel.cyber.dca.engine.experiment;

import coppelia.remoteApi;
import cz.cvut.fel.cyber.dca.engine.core.*;
import cz.cvut.fel.cyber.dca.engine.gui.ServiceLogger;
import cz.cvut.fel.cyber.dca.engine.util.StopWatch;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.security.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.*;
import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.simulationTimeMillis;

/**
 * Created by Jan on 25. 10. 2015.
 */
public class ExperimentController implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(ExperimentController.class.getName());

    private VrepSession session;
    private final SimpleBooleanProperty stopExperiment;
    private final StopWatch stopWatch;

    public ExperimentController() {
        this.session = new VrepSession();
        this.stopExperiment = new SimpleBooleanProperty(false);
        this.stopWatch = new StopWatch();
    }

    public SimpleBooleanProperty getStopExperiment() {
        return stopExperiment;
    }

    public SimpleBooleanProperty stopExperimentProperty() {
        return stopExperiment;
    }

    @Override
    public void run() {
        session = new VrepSession();
        stopWatch.start();
        if(session.connect()){
            LOGGER.log(Level.INFO,"Connected to V-Rep");
            ServiceLogger.log("Connected to V-Rep");
            session.getVrep().simxAddStatusbarMessage(session.getClientId(),"Connected from Java client!", session.getVrep().simx_opmode_oneshot);
        }else{
            LOGGER.log(Level.INFO,"Connection to V-Rep failed!");
            ServiceLogger.log("Connection to V-Rep failed");
            return;
        }

        Swarm.initMembers();
        Swarm.initVrepMembers(session);
        Swarm.launchDownloadBuffer(session);

        for(Quadrotor leader :  Swarm.getLeaders()){
            //Path3D path = new Path3D(leader.getId(),60,15);
            Path3D path = ConfigFileLoader.loadPath(leader.getId() + ".txt");
            Swarm.getPath3DList().add(path);
        }

        int iterationCounter = 0;
        while(!stopExperiment.get()){
            long startTime = System.currentTimeMillis();

            try {
                Thread.sleep(SIMULATION_STEP_MILLIS);
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING,e.getMessage());
            }

            LOGGER.log(Level.INFO, "Iteration no.: "  + ++iterationCounter);
            System.out.println("------------------------------------------------------------------------------------------------");
            System.out.println("Simulation running time: " + stopWatch.getTimeAsText());


            Swarm.downloadMembers(session);
            Swarm.loop(startTime);
            if(FLIGHT_RECORDING){
                BlackBoxDataCollector.logAll();                     // recorder exportData data
                BlackBoxDataCollector.writeRecord();
            }
            Swarm.uploadVrepMembers(session);
            getSimulationTime(session);
            ServiceLogger.logSimTime();
        }

        session.getVrep()
                .simxAddStatusbarMessage(session.getClientId(), "Disconnected from Java client!", session.getVrep().simx_opmode_oneshot);
        ServiceLogger.log("Discennected from V-Rep.");
        session.disconnect();
    }

    public void getSimulationTime(VrepSession session){
        CURRENT_SIMULATION_MILLIS = session.getVrep().simxGetLastCmdTime(session.getClientId());
    }



}
