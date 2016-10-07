package cz.cvut.fel.cyber.dca.engine.data;

import cz.cvut.fel.cyber.dca.engine.util.Vector3;

/**
 * Created by Jan on 28. 11. 2015.
 */
public class LeaderFollowInfo {

    private int timeStamp;
    private int incrementalTimeStamp;
    private int sourceId;
    private int hopCount;
    private Vector3 leaderDirection;
    private Vector3 leaderVelocity;


    public LeaderFollowInfo(int timeStamp, int incrementalTimeStamp, int sourceId, int hopCount, Vector3 leaderDirection, Vector3 leaderVelocity) {
        this.timeStamp = timeStamp;
        this.incrementalTimeStamp = incrementalTimeStamp;
        this.sourceId = sourceId;
        this.hopCount = hopCount;
        this.leaderDirection = leaderDirection;
        this.leaderVelocity = leaderVelocity;
    }

    public LeaderFollowInfo() {
        timeStamp = 0;
        incrementalTimeStamp = 0;
        sourceId = Integer.MAX_VALUE;
        hopCount = Integer.MAX_VALUE;
        leaderDirection = new Vector3();
        leaderVelocity = new Vector3();
    }

    public void setLeaderDirection(Vector3 leaderDirection) {
        this.leaderDirection = leaderDirection;
    }

    public void setLeaderVelocity(Vector3 leaderVelocity) {
        this.leaderVelocity = leaderVelocity;
    }

    public int getSourceId() {
        return sourceId;
    }

    public Vector3 getLeaderDirection() {
        return leaderDirection;
    }

    public Vector3 getLeaderVelocity() {
        return leaderVelocity;
    }

    public int getHopCount() {
        return hopCount;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public int getIncrementalTimeStamp() {
        return incrementalTimeStamp;
    }

    public LeaderFollowInfo copy(){
        return new LeaderFollowInfo(
                getTimeStamp(),
                getIncrementalTimeStamp()+1,
                getSourceId(),
                getHopCount()+1,
                getLeaderDirection(),
                getLeaderVelocity()
        );
    }

    @Override
    public String toString() {
        return "LeaderFollowInfo{" +
                "timeStamp = " + timeStamp +
                ",  incrementalTimeStamp = " + incrementalTimeStamp +
                ",  sourceId = " + sourceId +
                ",  hopCount = " + hopCount +
                ",  leaderDirection = " + leaderDirection +
                ",  leaderVelocity = " + leaderVelocity +
                '}';
    }
}
