package cz.cvut.fel.cyber.dca.engine.experiment;

import coppelia.IntW;
import cz.cvut.fel.cyber.dca.engine.core.Quadracopter;
import cz.cvut.fel.cyber.dca.engine.core.Unit;
import cz.cvut.fel.cyber.dca.engine.core.VrepSession;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.SIMULATION_STEP_MILLIS;

/**
 * Created by Jan on 17. 11. 2015.
 */
public class RelocTestController  implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(ExperimentController.class.getName());

    private VrepSession session;
    private final SimpleBooleanProperty stopExperiment;

    public RelocTestController() {
        this.session = new VrepSession();
        stopExperiment = new SimpleBooleanProperty(false);
    }

    @Override
    public void run() {
        session = new VrepSession();
        if(session.connect()){
            LOGGER.log(Level.INFO,"Connected to V-Rep");
            session.getVrep().simxAddStatusbarMessage(session.getClientId(),"Connected from Java client!",session.getVrep().simx_opmode_oneshot);
        }else{
            LOGGER.log(Level.INFO,"Connection to V-Rep failed!");
            return;
        }

        Unit quadricopter = new Quadracopter("Quadracopter","Quadricopter_target",true);

        IntW objectHandle = new IntW(0);
        IntW targetHandle = new IntW(0);

        int ret= session.getVrep().simxGetObjectHandle(session.getClientId(), quadricopter.getVrepUnitName()
                                                                ,objectHandle,session.getVrep().simx_opmode_oneshot_wait);
        if (ret==session.getVrep().simx_return_ok) {
            System.out.format("Object handel: " + objectHandle.getValue() + System.getProperty("line.separator"));
            quadricopter.setVrepObjectHandle(objectHandle.getValue());
        }
        ret=session.getVrep().simxGetObjectHandle(session.getClientId(), quadricopter.getVrepTargetName()
                                                                ,targetHandle,session.getVrep().simx_opmode_oneshot_wait);
        if (ret==session.getVrep().simx_return_ok) {
            System.out.format("Object handel: " + targetHandle.getValue() + System.getProperty("line.separator"));
            quadricopter.setVrepTargetHandle(targetHandle.getValue());
        }

        int iterationCounter = 0;
        while(!stopExperiment.get()){
            long startTime = System.currentTimeMillis();
            iterationCounter++;
            LOGGER.log(Level.INFO, "Iteration no.: "  + iterationCounter);



            try {
                Thread.sleep(SIMULATION_STEP_MILLIS);
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING,e.getMessage());
            }


        }

        session.getVrep()
                .simxAddStatusbarMessage(session.getClientId(), "Disconnected from Java client!", session.getVrep().simx_opmode_oneshot);

        session.disconnect();
    }



}