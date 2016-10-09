package cz.cvut.fel.cyber.dca.engine.core;

import cz.cvut.fel.cyber.dca.algorithms.AlgorithmLibrary;
import cz.cvut.fel.cyber.dca.engine.data.DensityData;
import cz.cvut.fel.cyber.dca.engine.data.LeaderFollowInfo;
import cz.cvut.fel.cyber.dca.engine.data.ThicknessDeterminationData;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.*;
import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * Created by Jan on 17. 11. 2015.
 */
public class Quadracopter extends Unit implements Loopable<Long,Void> {

    public static int leaderCount;

    protected boolean leader;
    protected boolean boundary;
    protected boolean convexBoundary;
    protected boolean tailRobot;

    protected double allowedHeightRangeMin;
    protected double allowedHeightRangeMax;

    private int iterationCounter;

    private List<Pair<Quadracopter, LeaderFollowInfo>> leaderInfo;
    private List<Pair<Quadracopter, ThicknessDeterminationData>> receivedStabilityImprovementData;

    private ThicknessDeterminationData thicknessDeterminationData;

    private DensityData densityData;

    private int lastCheckpoint;

    public Quadracopter(String vrepObjectName, String vrepTargetName, boolean leader) {
        super(vrepObjectName, vrepTargetName);

        this.leader = leader;
        this.boundary = false;
        this.convexBoundary = false;
        this.tailRobot = true;

        this.allowedHeightRangeMin = ROBOT_MIN_SAFETY_HEIGHT;
        this.allowedHeightRangeMax = ROBOT_MAX_SAFETY_HEIGHT;

        if(leader){
            leaderCount++;
        }
        iterationCounter = 0;
        lastCheckpoint = 0;
        leaderInfo = new ArrayList<>();
        thicknessDeterminationData = new ThicknessDeterminationData(iterationCounter,getId());
        receivedStabilityImprovementData = new ArrayList<>();
        densityData = new DensityData();
    }

    public Set<Quadracopter> getNeighbors(){
        Set<Quadracopter> neighbors = RobotGroup.getMembers().stream().filter(
                unit -> ROBOT_COMMUNICATION_RANGE > unit.getPosition().distance(getPosition())).collect(Collectors.toSet());
        neighbors = neighbors.stream().filter(unit -> unit.getId() != getId()).collect(Collectors.toSet());
        return neighbors;
    }

    public Set<Quadracopter> getReducedNeighbors(){
        return getNeighbors().stream().filter(neighbor -> {
            Vector3 position = getRelativeLocalization(neighbor);
            position.timesScalar(0.5);
            for(Quadracopter neigh2 : getNeighbors()){
                if(neigh2.equals(neighbor))continue;
                Vector3 position2 = getRelativeLocalization(neigh2);
                if(position.distance(position2)<position.norm3())return false;
            }
            return true;
        }).collect(Collectors.toSet());
    }

    public Set<Quadracopter> getVisibleNeighbors(){
        return getReducedNeighbors();
    }

    public int getLastCheckpoint() {
        return lastCheckpoint;
    }

    public void setLastCheckpoint(int lastCheckpoint) {
        this.lastCheckpoint = lastCheckpoint;
    }

    public boolean isLost(){
        return getNeighbors().isEmpty();
    }

    public boolean isBoundary() {
        return boundary;
    }

    public boolean isLeader() {
        return leader;
    }

    public boolean isConvexBoundary() {
        return convexBoundary;
    }

    public void setConvexBoundary(boolean convexBoundary) {
        this.convexBoundary = convexBoundary;
    }

    public boolean isTailRobot() {
        return tailRobot;
    }

    public void setTailRobot(boolean tailRobot) {
        this.tailRobot = tailRobot;
    }

    public int getIterationCounter() {
        return iterationCounter;
    }

    public Set<Quadracopter> getLeaders(){
        Set<Quadracopter> leaders = RobotGroup.getMembers().stream().filter(
                unit -> unit.isLeader()).collect(Collectors.toSet());
        return leaders;
    }

    public Set<Quadracopter> getBoundaryNeighbors(){
        Set<Quadracopter> boundaryNeighbors = getNeighbors().stream().filter(unit -> unit.isBoundary()).collect(Collectors.toSet());
        return boundaryNeighbors;
    }

    public Vector3 getRelativeLocalization(Vector3 pos){
        return Vector3.minus(getPosition(),pos);
    }

    public Vector3 getRelativeLocalization(Unit neighbor){
        if(neighbor==null)throw new NullPointerException();
        if(RobotGroup.getMembers().stream().filter(u -> u.getId()==neighbor.getId()).findAny().isPresent())
            return getRelativeLocalization(RobotGroup.getMembers().stream().filter(u -> u.getId()==neighbor.getId()).findAny().get().getPosition());
        else return new Vector3();
    }

    public Map<Quadracopter, Vector3> getNeighborRelLocMapper(){
        Map<Quadracopter, Vector3> relLocMap = new HashMap<>(0);
        getNeighbors().stream().forEach(neighbor -> relLocMap.put(neighbor,getRelativeLocalization(neighbor.getPosition())));
        return relLocMap;
    }

    public double getHeight(){
        return getPosition().getZ();
    }

    public HeightLayer getLayer(){
        if (RobotGroup.getLayerMapper().getLayerMapper().entrySet().stream().filter(entry -> entry.getKey().equals(this)).findFirst().isPresent())
            return RobotGroup.getLayerMapper().getLayerMapper().entrySet().stream().filter(entry -> entry.getKey().equals(this)).findFirst().get().getValue();
        else return null;
    }

    public double getDistance(Quadracopter neighbor){
        return getRelativeLocalization(neighbor).norm3();
    }

    public double getAllowedHeightRangeMin() {
        return allowedHeightRangeMin;
    }

    public double getAllowedHeightRangeMax() {
        return allowedHeightRangeMax;
    }

    public List<Pair<Quadracopter, LeaderFollowInfo>> getLeaderInfo() {
        return leaderInfo;
    }

    public ThicknessDeterminationData getThicknessDeterminationData(){
        return thicknessDeterminationData;
    }

    public List<Pair<Quadracopter, ThicknessDeterminationData>> getReceivedStabilityImprovementData() {
        return receivedStabilityImprovementData;
    }

    public void informNeighbors(){
        if(LEADER_FOLLOW_ALGORITHM_ACTIVATED) {
            if (isLeader())
                getReducedNeighbors().stream().forEach(neighbor -> neighbor.sendLeaderFollowInfo(this, new LeaderFollowInfo(getIterationCounter(),getIterationCounter(),
                        getId(), 0, getLinearVelocity().newUnitVector(), getLinearVelocity())));
            else getReducedNeighbors().stream()
                    //.filter(neighbor -> !leaderInfo.stream().map(info -> info.getKey()).collect(Collectors.toList()).contains(neighbor))
                    .forEach(neighbor -> leaderInfo.stream().forEach(leaderInfo -> {
                        /*if(!(leaderInfo.getKey().getId() == neighbor.getId()))*/ neighbor.sendLeaderFollowInfo(this, leaderInfo.getValue());
                    }));

            if (isLost()) leaderInfo.clear();
        }
        if(THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED) {
            getNeighbors().stream().forEach(neighbor -> neighbor.sendStabilityImprovementData(this, thicknessDeterminationData));
            getThicknessDeterminationData().setTimeStamp(getThicknessDeterminationData().getTimeStamp()+1);
        }
    }

    public void sendStabilityImprovementData(Quadracopter source, ThicknessDeterminationData data){
        if(receivedStabilityImprovementData.stream().filter(stData -> stData.getKey().equals(source)).findAny().isPresent()){
          Pair<Quadracopter,ThicknessDeterminationData> temporaryData =  receivedStabilityImprovementData.stream()
                                                        .filter(stData -> stData.getKey().equals(source)).findAny().get();
            if(temporaryData.getValue().getTimeStamp()<=data.getTimeStamp()){
                receivedStabilityImprovementData.remove(temporaryData);
                receivedStabilityImprovementData.add(new Pair<>(source,data));
            }
        }else receivedStabilityImprovementData.add(new Pair<>(source,data));
    }

    public void sendLeaderFollowInfo(Quadracopter source, LeaderFollowInfo info){
        if(leader)return;
        if(!getNeighbors().contains(source))return;
        if((info.getSourceId()==Integer.MAX_VALUE)||(info.getHopCount()==Integer.MAX_VALUE))return;

        if(leaderInfo.isEmpty()){
            leaderInfo.add(new Pair<>(source, mergeLeaderFollowInfo(source,info)));
            return;
        }
        if(leaderInfo.stream().filter(lfInfo -> lfInfo.getValue().getSourceId()==info.getSourceId()).findAny().isPresent()) {
            LeaderFollowInfo lfI = leaderInfo.stream().filter(lfInfo -> lfInfo.getValue().getSourceId()
                    == info.getSourceId()).findAny().get().getValue();
            if(source.isLeader()){
                leaderInfo = leaderInfo.stream().filter(lfI2 -> lfI2.getValue().getSourceId()!=info.getSourceId()).collect(Collectors.toList());
                leaderInfo.add(new Pair<>(source, mergeLeaderFollowInfo(source,info)));
                return;
            }

            if((lfI.getTimeStamp()>info.getTimeStamp())) return;

            if(lfI.getHopCount()>=info.getHopCount()){
                leaderInfo = leaderInfo.stream().filter(lfI2 -> lfI2.getValue().getSourceId()!=info.getSourceId()).collect(Collectors.toList());
                leaderInfo.add(new Pair<>(source, mergeLeaderFollowInfo(source,info)));
            }
        }else{
            leaderInfo.add(new Pair<>(source, mergeLeaderFollowInfo(source,info)));
        }
    }

    private LeaderFollowInfo mergeLeaderFollowInfo(Quadracopter source, LeaderFollowInfo info){


        if (source.isLeader() && (source.getId() == info.getSourceId())){
            Vector3 leaderRelativeLoc =  getRelativeLocalization(source).newUnitVector();
            leaderRelativeLoc.timesScalar(-1);
            return new LeaderFollowInfo(info.getTimeStamp(), info.getIncrementalTimeStamp() + 1 ,  info.getSourceId(), info.getHopCount() + 1,
                leaderRelativeLoc, info.getLeaderVelocity() );
        }

        Vector3 relativeLoc =  getRelativeLocalization(source).newUnitVector();
        relativeLoc.timesScalar(-1);
        Vector3 newLeaderDirection = Vector3.plus(info.getLeaderDirection().newUnitVector(),relativeLoc).newUnitVector();
        return new LeaderFollowInfo(info.getTimeStamp(), info.getIncrementalTimeStamp() + 1 , info.getSourceId(), info.getHopCount() + 1,
                newLeaderDirection, info.getLeaderVelocity() );

    }

    public DensityData getDensityData() {
        return densityData;
    }

    public String log(){
        return Double.toString(getPosition().getX()) + " " + Double.toString(getPosition().getY()) + " "  + Double.toString(getPosition().getZ()) + "\n" +
                    Double.toString(getLinearVelocity().getX()) + " " + Double.toString(getLinearVelocity().getY()) + " "  + Double.toString(getLinearVelocity().getZ()) + "\n";

    }

    @Override
    public Void loop(Long input) {
        iterationCounter++;
        System.out.println("LOOP " + iterationCounter +  " :::::::::: UAV ID = " + getId()
                                                + " ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("Measured velocity: " + getLinearVelocity());
        System.out.println("Measured position: " + getPosition());

        informNeighbors();


        System.out.println("Reduced neighborhood  of " + getId() + " ::" + getReducedNeighbors().size());
        getReducedNeighbors().stream().forEach(neighbor -> System.out.println(neighbor.getId()));

        Vector3 velocity = new Vector3();

        if(BOUNDARY_DETECTION_ALGORITHM_ACTIVATED) {
            this.boundary = AlgorithmLibrary.getBoundaryDetectionAlgorithm().loop(this);
            System.out.println(getId() + " >> Boundary robot: " + isBoundary());
        }

        Vector3 boundaryForce = new Vector3();
        if(BOUNDARY_DETECTION_ALGORITHM_ACTIVATED && BOUNDARY_TENSION_ALGORITHM_ACTIVATED) {
            if (boundary) {
                boundaryForce = AlgorithmLibrary.getBoundaryTensionAlgorithm().loop(this);
                System.out.println("Boundary force of " + getId() + " :: " + boundaryForce.toString());
                System.out.println("Covex boundary robot " + getId() +" :: " + isConvexBoundary()  );
                System.out.println("Tail robot " + getId() +" :: " + isTailRobot()  );
            }
        }

        Vector3 leaderForces = new Vector3();
        if(LEADER_FOLLOW_ALGORITHM_ACTIVATED){
            leaderForces = AlgorithmLibrary.getLeaderFollowAlgorithm().loop(this);
            if(leader&&!LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED)return null;
        }

        if(THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED){
            Vector3 thicnkessContractionForce = AlgorithmLibrary.getThicknessDeterminationNContractionAlgorithm().loop(this);
            System.out.println("Thickness data of " + getId() + " :: " + thicknessDeterminationData.toString());
            System.out.println("Thickness contraction force1 of " + getId() + " :: " + thicnkessContractionForce.toString());
        }

        Vector3 densityForce = new Vector3();
        if(DENSITY_ALGORITHM_ACTIVATED){
            densityForce = AlgorithmLibrary.getDensityAlgorithm().loop(this);
            System.out.println("Desnity force of " + getId() + " :: " + densityForce.toString());
            System.out.println("Desnity data of " + getId() + " :: " + densityData.toString());
        }
/*
        if(FLOCKING_ALGORITHM_ACTIVATED){
            Vector3 acceleration = AlgorithmLibrary.getFlockingAlgorithm().loop(this);
            System.out.println(getId() + ">> Acceleration >> " + acceleration.toString());
            //if(acceleration.isVectorNull())return null;
            velocity = Vector3.timesScalar(acceleration,(double) (System.currentTimeMillis() - input.longValue()) / 1000 );

            velocity.timesScalar((double)(System.currentTimeMillis()-input.longValue())/1000);
        }
        */
        if(BOID_ALGORITHM_ACTIVATED){
            velocity = AlgorithmLibrary.getBoidAlgorithm().loop(this);

            velocity.timesScalar((double)(System.currentTimeMillis()-input.longValue())/1000);
        }
        if(FLOCKING_ALGORITHM_ACTIVATED){
            velocity = AlgorithmLibrary.getFlockingAlgorithm().loop(this);

            velocity.timesScalar((double)(System.currentTimeMillis()-input.longValue())/1000);
        }

        if(boundary && BOUNDARY_TENSION_ALGORITHM_ACTIVATED)
            velocity.plus(Vector3.timesScalar(boundaryForce,(double) (System.currentTimeMillis() - input.longValue()) / 1000));
        if(!leader && LEADER_FOLLOW_ALGORITHM_ACTIVATED)
            velocity.plus(Vector3.timesScalar(leaderForces,(double) (System.currentTimeMillis() - input.longValue()) / 1000));
        if(DENSITY_ALGORITHM_ACTIVATED)
            velocity.plus(Vector3.timesScalar(densityForce,(double) (System.currentTimeMillis() - input.longValue()) / 1000));

        if(isLeader()){
            if(LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED){
                velocity = AlgorithmLibrary.getFollowPathAlgorithm().loop(this);

                velocity.timesScalar((double)(System.currentTimeMillis()-input.longValue())/1000);
                System.out.println("Leader follows checkpoints  :::: " + getId() + " ::::: " + velocity.toString());

            }else{
                return null;
            }
        }


        for(Pair lfa : leaderInfo){
            System.out.println("LFInfo source: " + ((Quadracopter)lfa.getKey()).getId());
            System.out.println("LFInfo:  " + ((LeaderFollowInfo)lfa.getValue()).toString());
        }
/*
        if(HEIGHT_SAFETY_CONTROL_ACTIVATED){
            velocity = AlgorithmLibrary.getSimpleHeightSafetyControlAlgorithm().loop(new Pair<>(this,velocity));
        }
        if(HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED){
            Vector3 heightForce = AlgorithmLibrary.getHeightLayerControlAlgorithm().loop(this);
            velocity.plus(Vector3.timesScalar(heightForce,(double) (System.currentTimeMillis() - input.longValue()) / 1000));
        }
*/
        if(velocity.norm3()>ROBOT_MAX_VELOCITY)velocity.timesScalar(ROBOT_MAX_VELOCITY/velocity.norm3());

        System.out.println(getId() + ">> Velocity >> " + velocity.toString());
        if((getTargetPosition().distance(getPosition())<0.05)){
            if(!getTargetPosition().isVectorNull()){
                Vector3 newPos = Vector3.plus(getPosition(),velocity);
                setTargetPosition(newPos);
            }
        }
        return null;
    }

}
