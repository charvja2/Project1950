package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.data.LeaderFollowInfo;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;

import java.util.*;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.*;

/**
 * Created by Jan on 28. 10. 2015.
 */
public class LeaderFollowAlgorithm implements Loopable<Quadrotor,Vector3>{

    private double param = -1.6;

    public double getParam() {
        return param;
    }

    public void setParam(double param) {
        this.param = param;
    }

    private Vector3 getLeaderForces(Quadrotor unit){
        Set<Quadrotor> leaders = unit.getLeaders();
        List<Pair<Quadrotor, LeaderFollowInfo>> leaderInfoList = unit.getLeaderInfo();

        int leadersWithoutInfo = leaders.size() - leaderInfoList.size();

        if(leaders.isEmpty())return new Vector3();
        if(leaderInfoList.isEmpty())return new Vector3();
        if(leaderInfoList.size()!=leaders.size())return new Vector3();



        double sum = 0;
        for(Pair leader : leaderInfoList) {
            sum += (((double)1) / ((LeaderFollowInfo)leader.getValue()).getHopCount());
        }

        Vector3 leaderForce = new Vector3();
        if(sum == 0) return leaderForce;

        for(Pair leader : leaderInfoList) {
            Quadrotor source = ((Quadrotor) leader.getKey());
            LeaderFollowInfo info = (LeaderFollowInfo) leader.getValue();

            double velocity = ((LeaderFollowInfo)leader.getValue()).getLeaderVelocity().norm3();
            Vector3 cl = ((LeaderFollowInfo)leader.getValue()).getLeaderDirection();

            if((info.getHopCount() == 1) && (source.getId() == info.getSourceId())
                    && (unit.getDistance(source)>(ROBOT_DESIRED_DISTANCE-(ROBOT_DESIRED_DISTANCE/3))))
                velocity *= 2.5*unit.getDistance(source)/ ROBOT_DESIRED_DISTANCE ;

            cl.timesScalar(velocity);
            cl.timesScalar((((double)1)/((LeaderFollowInfo)leader.getValue()).getHopCount())/sum);
            //double konst = ((double)1)/(((LeaderFollowInfo)leader.getValue()).getHopCount());
            //cl.timesScalar(konst);
            leaderForce.plus(cl);
        }
        return leaderForce;
    }

    @Override
    public Vector3 loop(Quadrotor input) {
        if(input.isLeader())return new Vector3();
        Vector3 leaderForces = getLeaderForces(input);
        leaderForces.timesScalar(param);
        if(DIMENSION == 2)leaderForces.setZ(0);
        System.out.println("Leader force of " + input.getId() + " :: " + leaderForces.toString());
        return leaderForces;
    }
}
