package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadracopter;
import cz.cvut.fel.cyber.dca.engine.data.LeaderFollowInfo;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;

import java.util.*;

/**
 * Created by Jan on 28. 10. 2015.
 */
public class LeaderFollowAlgorithm implements Loopable<Quadracopter,Vector3>{

    private double param = 1.5;

    private Quadracopter current;
    private PriorityQueue<Quadracopter> open;
    private Set<Quadracopter> close;
    private HashMap<Quadracopter, Double> g;
    private HashMap<Quadracopter, Double> h;
    private HashMap<Quadracopter, Quadracopter> parrent;
    private List<Quadracopter> tmp, minimumHopTree;

    private double euklidDist(Quadracopter currentNode, Quadracopter endNode) {
       return currentNode.getPosition().distance(endNode.getPosition());
    }

    private double fFunction(Quadracopter node) {
        return h.get(node).doubleValue() + g.get(node).doubleValue();
    }

    private List<Quadracopter> minimumHopTree(Quadracopter startNode, Quadracopter leader){
        boolean notAdd = false;

        tmp = new LinkedList<>();
        minimumHopTree = new LinkedList<>();

        open = new PriorityQueue<>(1, (Comparator<Quadracopter>) (o1, o2) -> Double.compare(fFunction(o1), fFunction(o2)));
        close = new HashSet<>();

        g = new HashMap<>();
        h = new HashMap<>();

        parrent = new HashMap<>();

        g.put(startNode, new Double(0));
        h.put(startNode, new Double(euklidDist(startNode, leader)));

        open.add(startNode);

        while (!open.isEmpty()) {
            current = open.poll();
            close.add(current);

            if (current.equals(leader)) {
                break;
            }
            tmp = new LinkedList<>(current.getNeighbors());

            if (!tmp.isEmpty()) {
                for (Quadracopter node : tmp) {
                    if ((close.contains(node)) || (open.contains(node))) {
                        if (g.get(node).doubleValue() > (g.get(current).doubleValue() + euklidDist(current, node))) {
                            g.put(node, new Double(g.get(current).doubleValue() + euklidDist(current,node)));
                            parrent.put(node, current);
                            if (close.contains(node)) {
                                close.remove(node);
                                open.add(node);
                            }
                        }
                        notAdd = true;
                    }

                    if (!notAdd) {
                        parrent.put(node, current);
                        g.put(node, new Double(g.get(current).doubleValue() + euklidDist(current, node)));
                        h.put(node, new Double(euklidDist(node, leader)));

                        open.add(node);
                    }
                    notAdd = false;
                }
            }
        }

        tmp.clear();

        while (!current.equals(startNode)) {
            tmp.add(current);
            current = parrent.get(current);
        }
        tmp.add(current);

        for (int i = tmp.size() - 1; i >= 0; i--) {
            minimumHopTree.add(tmp.get(i));
        }

        return minimumHopTree;
    }

    private Vector3 getLeaderForces(Quadracopter unit){
        Set<Quadracopter> leaders = unit.getLeaders();
        List<Pair<Quadracopter, LeaderFollowInfo>> leaderInfoList = unit.getLeaderInfo();

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
            Quadracopter source = ((Quadracopter) leader.getKey());
            LeaderFollowInfo info = (LeaderFollowInfo) leader.getValue();

            double velocity = ((LeaderFollowInfo)leader.getValue()).getLeaderVelocity().norm3();
            Vector3 cl = ((LeaderFollowInfo)leader.getValue()).getLeaderDirection();

            if((info.getHopCount() == 1) && (source.getId() == info.getSourceId())
                    && (unit.getDistance(source)>(Experiment.ROBOT_DESIRED_DISTANCE-(Experiment.ROBOT_DESIRED_DISTANCE/3))))
                velocity *= 2.5*unit.getDistance(source)/Experiment.ROBOT_DESIRED_DISTANCE ;

            cl.timesScalar(velocity);
            cl.timesScalar((((double)1)/((LeaderFollowInfo)leader.getValue()).getHopCount())/sum);
            //double konst = ((double)1)/(((LeaderFollowInfo)leader.getValue()).getHopCount());
            //cl.timesScalar(konst);
            leaderForce.plus(cl);
        }
        return leaderForce;
    }

    @Override
    public Vector3 loop(Quadracopter input) {
        if(input.isLeader())return new Vector3();
        Vector3 leaderForces = getLeaderForces(input);
        leaderForces.timesScalar(param);
        //leaderForces.setZ(0);
        System.out.println("Leader force of " + input.getId() + " :: " + leaderForces.toString());
        return leaderForces;
    }
}
