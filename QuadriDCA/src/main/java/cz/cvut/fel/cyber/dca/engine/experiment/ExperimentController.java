package cz.cvut.fel.cyber.dca.engine.experiment;

import cz.cvut.fel.cyber.dca.engine.core.*;
import cz.cvut.fel.cyber.dca.engine.util.StopWatch;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.*;

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
            session.getVrep().simxAddStatusbarMessage(session.getClientId(),"Connected from Java client!",session.getVrep().simx_opmode_oneshot);
        }else{
            LOGGER.log(Level.INFO,"Connection to V-Rep failed!");
            return;
        }

        RobotGroup.initMembers();
        RobotGroup.initVrepMembers(session);
        RobotGroup.launchDownloadBuffer(session);

        if(LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED){
            for(Quadracopter leader :  RobotGroup.getLeaders()){
                Path path = new Path(leader.getId(),CHECKPOINT_COUNT);
                path.initVrep(session);
                RobotGroup.getPathList().add(path);
            }
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


            RobotGroup.downloadMembers(session);
            RobotGroup.loop(startTime);
            if(FLIGHT_RECORDING){
                BlackBoxDataCollector.logAll();                     // recorder log data
                BlackBoxDataCollector.writeRecord();
            }
            RobotGroup.updateProfile();
            RobotGroup.uploadVrepMembers(session);
        }

        session.getVrep()
                .simxAddStatusbarMessage(session.getClientId(), "Disconnected from Java client!", session.getVrep().simx_opmode_oneshot);

        session.disconnect();
    }



}
