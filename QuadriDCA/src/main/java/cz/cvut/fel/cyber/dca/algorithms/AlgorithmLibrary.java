package cz.cvut.fel.cyber.dca.algorithms;

/**
 * Created by Jan on 28. 10. 2015.
 */
public class AlgorithmLibrary {

    private static BoidAlgorithm boidAlgorithm = new BoidAlgorithm();
    private static FlockingAlgorithm flockingAlgorithm = new FlockingAlgorithm();
    private static BoundaryDetection boundaryDetectionAlgorithm = new BoundaryDetection();
    private static BoundaryTension boundaryTensionAlgorithm = new BoundaryTension();
    private static LeaderFollowAlgorithm leaderFollowAlgorithm = new LeaderFollowAlgorithm();
    private static ThicknessDeterminationNContractionAlgorithm thicknessDeterminationNContractionAlgorithm = new ThicknessDeterminationNContractionAlgorithm();
    private static DensityAlgorithm densityAlgorithm = new DensityAlgorithm();
    private static FollowPathAlgorithm followPathAlgorithm = new FollowPathAlgorithm();
    private static SimpleHeightSafetyControlAlgorithm simpleHeightSafetyControlAlgorithm = new SimpleHeightSafetyControlAlgorithm();
    private static HeightLayerControlAlgorithm heightLayerControlAlgorithm = new HeightLayerControlAlgorithm();

    public static FlockingAlgorithm getFlockingAlgorithm() {
        return flockingAlgorithm;
    }

    public static BoundaryDetection getBoundaryDetectionAlgorithm() {
        return boundaryDetectionAlgorithm;
    }

    public static LeaderFollowAlgorithm getLeaderFollowAlgorithm() {
        return leaderFollowAlgorithm;
    }

    public static ThicknessDeterminationNContractionAlgorithm getThicknessDeterminationNContractionAlgorithm() {
        return thicknessDeterminationNContractionAlgorithm;
    }

    public static BoidAlgorithm getBoidAlgorithm() {
        return boidAlgorithm;
    }

    public static BoundaryTension getBoundaryTensionAlgorithm() {
        return boundaryTensionAlgorithm;
    }

    public static DensityAlgorithm getDensityAlgorithm() {
        return densityAlgorithm;
    }

    public static FollowPathAlgorithm getFollowPathAlgorithm() {
        return followPathAlgorithm;
    }

    public static SimpleHeightSafetyControlAlgorithm getSimpleHeightSafetyControlAlgorithm() {
        return simpleHeightSafetyControlAlgorithm;
    }

    public static HeightLayerControlAlgorithm getHeightLayerControlAlgorithm() {
        return heightLayerControlAlgorithm;
    }
}
