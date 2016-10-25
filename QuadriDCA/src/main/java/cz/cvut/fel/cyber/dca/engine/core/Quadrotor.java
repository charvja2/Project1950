package cz.cvut.fel.cyber.dca.engine.core;

import cz.cvut.fel.cyber.dca.algorithms.AlgorithmLibrary;
import cz.cvut.fel.cyber.dca.engine.data.DensityData;
import cz.cvut.fel.cyber.dca.engine.data.LeaderFollowInfo;
import cz.cvut.fel.cyber.dca.engine.data.ThicknessDeterminationData;
import cz.cvut.fel.cyber.dca.engine.experiment.Experiment;
import cz.cvut.fel.cyber.dca.engine.gui.ServiceLogger;
import cz.cvut.fel.cyber.dca.engine.util.LogicalVector;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;
import sun.plugin.javascript.navig4.Layer;

import java.util.*;
import java.util.stream.Collectors;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.*;
import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * Created by Jan on 17. 11. 2015.
 */
public class Quadrotor extends Unit implements Loopable<Long,Void> {

    public static int leaderCount;

    protected boolean leader;
    protected boolean boundary;
    protected boolean convexBoundary;
    protected boolean tailRobot;

    protected LogicalVector boundaryVector;
    protected LogicalVector convexBoundaryVector;

    protected boolean landingCmd;
    protected boolean takeOffCmd;
    protected boolean failure;

    protected double allowedHeightRangeMin;
    protected double allowedHeightRangeMax;

    private int iterationCounter;

    // leader follow data and received thickness data
    private List<Pair<Quadrotor, LeaderFollowInfo>> leaderInfo;
    private List<Pair<Quadrotor, ThicknessDeterminationData>> receivedStabilityImprovementData;

    // thickness and density data
    private ThicknessDeterminationData thicknessDeterminationData;
    private DensityData densityData;

    private int lastCheckpoint;

    private HeightProfile heightProfile;
    private Layer optimalLayer;

    public Quadrotor(String vrepObjectName, String vrepTargetName, boolean leader) {
        super(vrepObjectName, vrepTargetName);

        this.leader = leader;
        this.boundary = false;
        this.convexBoundary = false;
        this.tailRobot = true;

        this.boundaryVector = new LogicalVector();
        this.convexBoundaryVector = new LogicalVector();

        this.takeOffCmd = false;
        this.landingCmd = false;
        this.failure = false;

        if(leader){
            leaderCount++;
        }
        this.iterationCounter = 0;
        this.lastCheckpoint = 0;
        this.leaderInfo = new ArrayList<>();
        this.thicknessDeterminationData = new ThicknessDeterminationData(iterationCounter,getId());
        this.receivedStabilityImprovementData = new ArrayList<>();
        this.densityData = new DensityData();

        this.heightProfile = initHeightProfile();

        this.allowedHeightRangeMin = ROBOT_MIN_SAFETY_HEIGHT;
        this.allowedHeightRangeMax = ROBOT_MAX_SAFETY_HEIGHT;
    }

    public Set<Quadrotor> getNeighbors(){
        Set<Quadrotor> neighbors = Swarm.getMembers().stream().filter(
                unit -> ROBOT_COMMUNICATION_RANGE > unit.getPosition().distance(getPosition())).collect(Collectors.toSet());
        neighbors = neighbors.stream().filter(unit -> unit.getId() != getId()).collect(Collectors.toSet());
        neighbors = neighbors.stream().filter(unit -> !unit.isLandingCmd()&&!unit.isFailure()).collect(Collectors.toSet());
        return neighbors;
    }

    public Set<Quadrotor> getReducedNeighbors(){
        return getNeighbors().stream().filter(neighbor -> {
            Vector3 position = getRelativeLocalization(neighbor);
            position.timesScalar(0.5);
            for(Quadrotor neigh2 : getNeighbors()){
                if(neigh2.equals(neighbor))continue;
                Vector3 position2 = getRelativeLocalization(neigh2);
                if(position.distance(position2)<position.norm3())return false;
            }
            return true;
        }).collect(Collectors.toSet());
    }

    public Set<Quadrotor> getVisibleNeighbors(){
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

    public Set<Quadrotor> getLeaders(){
        Set<Quadrotor> leaders = Swarm.getMembers().stream().filter(
                unit -> unit.isLeader()).collect(Collectors.toSet());
        return leaders;
    }

    public Set<Quadrotor> getBoundaryNeighbors(){
        Set<Quadrotor> boundaryNeighbors = getNeighbors().stream().filter(unit -> unit.isBoundary()).collect(Collectors.toSet());
        return boundaryNeighbors;
    }

    public Vector3 getRelativeLocalization(Vector3 pos){
        return Vector3.minus(pos,getPosition());
    }

    public Vector3 getRelativeLocalization(Unit neighbor){
        if(neighbor==null)throw new NullPointerException();
        if(Swarm.getMembers().stream().filter(u -> u.getId()==neighbor.getId()).findAny().isPresent())
            return getRelativeLocalization(Swarm.getMembers().stream().filter(u -> u.getId()==neighbor.getId()).findAny().get().getPosition());
        else return new Vector3();
    }

    public Map<Quadrotor, Vector3> getNeighborRelLocMapper(){
        Map<Quadrotor, Vector3> relLocMap = new HashMap<>(0);
        getNeighbors().stream().forEach(neighbor -> relLocMap.put(neighbor,getRelativeLocalization(neighbor.getPosition())));
        return relLocMap;
    }

    public double getHeight(){
        return getPosition().getZ();
    }

    public HeightProfile getHeightProfile() {
        return heightProfile;
    }

    public HeightLayer getCurrentLayer(){
        if (getHeightProfile().getLayers().stream().filter(layer -> layer.inRange(getHeight())).findAny().isPresent())
            return getHeightProfile().getLayers().stream().filter(layer -> layer.inRange(getHeight())).findAny().get();
        return null;
    }

    private HeightProfile initHeightProfile(){
        HeightProfile profile = new HeightProfile();
        for(int i = 0; i <= ROBOT_MAX_SAFETY_HEIGHT; i+= HEIGHT_LAYER_HEIGHT){
            profile.getLayers().add(new HeightLayer(i,i+HEIGHT_LAYER_HEIGHT));
        }
        return profile;
    }

    public double getDistance(Quadrotor neighbor){
        return getRelativeLocalization(neighbor).norm3();
    }

    public double getAllowedHeightRangeMin() {
        return allowedHeightRangeMin;
    }

    public double getAllowedHeightRangeMax() {
        return allowedHeightRangeMax;
    }

    public boolean isTakeOffCmd() {
        return takeOffCmd;
    }

    public void setTakeOffCmd(boolean takeOffCmd) {
        this.takeOffCmd = takeOffCmd;
    }

    public boolean isLandingCmd() {
        return landingCmd;
    }

    public void setLandingCmd(boolean landingCmd) {
        this.landingCmd = landingCmd;
    }

    public boolean isFailure() {
        return failure;
    }

    public void setFailure(boolean failure) {
        this.failure = failure;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public LogicalVector getBoundaryVector() {
        return boundaryVector;
    }

    public LogicalVector getConvexBoundaryVector() {
        return convexBoundaryVector;
    }

    public List<Pair<Quadrotor, LeaderFollowInfo>> getLeaderInfo() {
        return leaderInfo;
    }

    public ThicknessDeterminationData getThicknessDeterminationData(){
        return thicknessDeterminationData;
    }

    public List<Pair<Quadrotor, ThicknessDeterminationData>> getReceivedStabilityImprovementData() {
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
            getReducedNeighbors().stream().forEach(neighbor -> neighbor.sendStabilityImprovementData(this, thicknessDeterminationData));
            getThicknessDeterminationData().setTimeStamp(getThicknessDeterminationData().getTimeStamp()+1);
            receivedStabilityImprovementData = receivedStabilityImprovementData.stream()
                            .filter(data -> !getReducedNeighbors().contains(data.getKey())).collect(Collectors.toList());
        }
    }

    public void sendStabilityImprovementData(Quadrotor source, ThicknessDeterminationData data){
        if(receivedStabilityImprovementData.stream().filter(stData -> stData.getKey().equals(source)).findAny().isPresent()){
          Pair<Quadrotor,ThicknessDeterminationData> temporaryData =  receivedStabilityImprovementData.stream()
                                                        .filter(stData -> stData.getKey().equals(source)).findAny().get();
            if(temporaryData.getValue().getTimeStamp()<=data.getTimeStamp()){
                receivedStabilityImprovementData.remove(temporaryData);
                receivedStabilityImprovementData.add(new Pair<>(source,data));
            }
        }else receivedStabilityImprovementData.add(new Pair<>(source,data));
    }

    public void sendLeaderFollowInfo(Quadrotor source, LeaderFollowInfo info){
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

    private LeaderFollowInfo mergeLeaderFollowInfo(Quadrotor source, LeaderFollowInfo info){


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

    public String getLogMessage(Vector3 velocity){
        if(isFailure()) return "Failure.";
        String message = isLeader() ? "L " : "   ";
        message += "B=" + getBoundaryVector().toString() + "= " + isBoundary() + "\tPOS= " + getPosition().toStringRounded()
                + "\tVEL= " + velocity.toStringRounded() + "\tLAYER= " + getHeightProfile().getLayers().indexOf(getCurrentLayer());
        message += THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED ? ( "\t" +thicknessDeterminationData.toLogString()) : "" ;
        message += DENSITY_ALGORITHM_ACTIVATED ? (  "\t" + densityData.toLogString()) : "";
        return message;
    }

    public String exportData(){
        return Double.toString(getPosition().getX()) + " " + Double.toString(getPosition().getY()) + " "  + Double.toString(getPosition().getZ()) + "\n" +
                    Double.toString(getLinearVelocity().getX()) + " " + Double.toString(getLinearVelocity().getY()) + " "  + Double.toString(getLinearVelocity().getZ()) + "\n";

    }

    @Override
    public Void loop(Long input) {
        iterationCounter++;
        System.out.println("LOOP " + iterationCounter +  " :::::::::: UAV ID = " + getId()
                                        + " ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("Measured velocity: " + getLinearVelocity().toStringRounded());
        System.out.println("Measured position: " + getPosition().toStringRounded());

        informNeighbors();

        Vector3 velocity = new Vector3();

        if(BOUNDARY_DETECTION_ALGORITHM_ACTIVATED) {
            if(DIMENSION == 2) this.boundary = AlgorithmLibrary.getBoundaryDetectionAlgorithm().loop(this);
            else this.boundary = AlgorithmLibrary.getBoundaryDetection3DAlgorithm().loop(this);
            System.out.println(getId() + " >> Boundary robot: " + isBoundary());
        }

        Vector3 boundaryForce = new Vector3();
        if(BOUNDARY_DETECTION_ALGORITHM_ACTIVATED && BOUNDARY_TENSION_ALGORITHM_ACTIVATED) {
            if (boundary) {
                if(DIMENSION==2){
                    boundaryForce = AlgorithmLibrary.getBoundaryTensionAlgorithm().loop(this);
                    setConvexBoundary(getConvexBoundaryVector().isX());
                }
                else {
                    boundaryForce = AlgorithmLibrary.getBoundaryTension3DAlgorithm().loop(this);
                    setConvexBoundary(getConvexBoundaryVector().and());
                }

                System.out.println("Boundary force (" + getId() + ") : " + boundaryForce.toStringRounded());
                //ServiceLogger.log("Boundary force (" + getId() + ") : " + boundaryForce.toStringRounded());
                System.out.println("Covex boundary robot (" + getId() +") : " + isConvexBoundary()  );
                System.out.println("Tail robot (" + getId() +") : " + isTailRobot()  );
            }
        }

        Vector3 leaderForces = new Vector3();
        if(LEADER_FOLLOW_ALGORITHM_ACTIVATED){
            leaderForces = AlgorithmLibrary.getLeaderFollowAlgorithm().loop(this);
            if(leader&&!LEADER_FOLLOWS_CHECKPOINTS_ACTIVATED)return null;
        }

        Vector3 thicknessContractionForce = new Vector3();
        if(THICKNESS_DETERMINATION_ALGORITHM_ACTIVATED){
            thicknessContractionForce = AlgorithmLibrary.getThicknessDeterminationNContractionAlgorithm().loop(this);
            System.out.println("Thickness data (" + getId() + ") : " + thicknessDeterminationData.toString());
            System.out.println("Thickness force (" + getId() + ") : " + thicknessContractionForce.toStringRounded());
           /* ServiceLogger.log("Thickness data (" + getId() + ") :\t " + thicknessDeterminationData.toString());
            ServiceLogger.log("Thickness force ( " + getId() + ") :\t " + thicknessContractionForce.toStringRounded());*/
        }

        Vector3 densityForce = new Vector3();
        if(DENSITY_ALGORITHM_ACTIVATED){
            densityForce = AlgorithmLibrary.getDensityAlgorithm().loop(this);
            System.out.println("Desnity force (" + getId() + ") : " + densityForce.toStringRounded());
            System.out.println("Desnity data (" + getId() + ") : " + densityData.toString());
            /*ServiceLogger.log("Desnity force (" + getId() + ") :\t " + densityForce.toStringRounded());
            ServiceLogger.log("Desnity data (" + getId() + ") :\t " + densityData.toString());*/
        }

        Vector3 acceleration = new Vector3();
        if(FLOCKING_ALGORITHM_ACTIVATED){
            acceleration = AlgorithmLibrary.getFlockingAlgorithm().loop(this);
            System.out.println("Flocking force (" + getId() + ") : " + acceleration.toStringRounded());
            //ServiceLogger.log("Flocking force (" + getId() + ") :\t " + acceleration.toStringRounded());
            velocity = Vector3.timesScalar(acceleration,(double) (System.currentTimeMillis() - input.longValue()) / 1000 );

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
                System.out.println("Follow path force (" + getId() + ") :\t " + velocity.toStringRounded());

            }else if(!HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED){
                ServiceLogger.logLabel(this, getLogMessage(new Vector3()));
                return null;
            }
        }


        for(Pair lfa : leaderInfo){
            System.out.println("LFInfo source: " + ((Quadrotor)lfa.getKey()).getId());
            System.out.println("LFInfo:  " + ((LeaderFollowInfo)lfa.getValue()).toString());
        }

        if(HEIGHT_SAFETY_CONTROL_ACTIVATED){
            velocity = AlgorithmLibrary.getSimpleHeightSafetyControlAlgorithm().loop(new Pair<>(this,velocity));
        }
        if(HEIGHT_LAYER_CONTROL_ALGORITHM_ACTIVATED){
            Vector3 heightForce = AlgorithmLibrary.getHeightLayerControlAlgorithm().loop(this);
            velocity.plus(Vector3.timesScalar(heightForce,(double) (System.currentTimeMillis() - input.longValue()) / 1000));
            System.out.println("Height force (" + getId() + ") : " + heightForce.toStringRounded());
        }

        if(velocity.norm3()>ROBOT_MAX_VELOCITY)velocity.timesScalar(ROBOT_MAX_VELOCITY/velocity.norm3());

        System.out.println(getId() + ">> Velocity >> " + velocity.toString());

        ServiceLogger.logLabel(this, getLogMessage(velocity));

        if((getTargetPosition().distance(getPosition())<0.05)){
            if(!getTargetPosition().isVectorNull()){
                Vector3 newPos = Vector3.plus(getPosition(),velocity);
                if(isFailure())newPos.setZ(0);
                setTargetPosition(newPos);
            }
        }
        return null;
    }

}
