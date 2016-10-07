package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.*;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 13. 12. 2015.
 */
public class FollowPathAlgorithm implements Loopable<Quadracopter, Vector3> {

    @Override
    public Vector3 loop(Quadracopter input) {
        List<DummyCheckpoint> checkpointList = new ArrayList<>();
        List<Path> pathList = RobotGroup.getPathList();
        if (RobotGroup.getPathList().stream().filter(path -> path.getIndex()==input.getId()).findAny().isPresent())
            checkpointList = RobotGroup.getPathList().stream().filter(path -> path.getIndex()==input.getId())
                                                                                    .findAny().get().getCheckpointList();

        if(checkpointList.size()==0)return new Vector3();
        if(checkpointList.size()==input.getLastCheckpoint())return new Vector3();

        DummyCheckpoint checkpoint =  checkpointList.get(input.getLastCheckpoint());

        if(checkpoint.getPosition().distance(input.getPosition())<(input.getPosition().getZ()+0.4)){
            input.setLastCheckpoint(input.getLastCheckpoint()+1);
            return new Vector3();
        }

        Vector3 position = Vector3.minus(checkpoint.getPosition().toVector2().toVector3(),input.getPosition().toVector2().toVector3());
        //position.timesScalar();
        //position.setZ(0);

        return position;
    }
}
