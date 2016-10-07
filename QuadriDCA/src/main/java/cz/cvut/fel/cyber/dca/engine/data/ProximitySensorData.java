package cz.cvut.fel.cyber.dca.engine.data;

import coppelia.BoolW;
import coppelia.FloatWA;
import coppelia.IntW;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;

/**
 * Created by Jan on 11. 11. 2015.
 */
public class ProximitySensorData {

    private final boolean detectionState;
    private final Vector3 detectedPoint;
    private final int detectedObjectHandle;
    private final Vector3 detectedSurfaceNormalVector;

    public ProximitySensorData(BoolW detectionState, FloatWA detectedPoint,
                               IntW detectedObjectHandle, FloatWA detectedSurfaceNormalVector) {
        this.detectionState =  detectionState == null ? null : detectionState.getValue();
        this.detectedPoint = detectedPoint == null ? null : new Vector3(detectedPoint.getArray()[0]
                ,detectedPoint.getArray()[1],detectedPoint.getArray()[2]);
        this.detectedObjectHandle = detectedObjectHandle == null ? 0 : detectedObjectHandle.getValue();
        this.detectedSurfaceNormalVector = detectedSurfaceNormalVector == null ? null : new Vector3(detectedSurfaceNormalVector.getArray()[0]
                ,detectedSurfaceNormalVector.getArray()[1],detectedSurfaceNormalVector.getArray()[2]);
    }

    public boolean isDetectionState() {
        return detectionState;
    }

    public Vector3 getDetectedPoint() {
        return detectedPoint;
    }

    public int getDetectedObjectHandle() {
        return detectedObjectHandle;
    }

    public Vector3 getDetectedSurfaceNormalVector() {
        return detectedSurfaceNormalVector;
    }

    @Override
    public String toString() {
        return "ProximitySensorData{" +
                "detectionState\t\t\t" + detectionState  + System.getProperty("line.separator") +
                "detectedPoint\t\t\t" + detectedPoint ==null ? "null" : detectedPoint.toString() + System.getProperty("line.separator") +
                "detectedObjectHandle\t\t" + detectedObjectHandle + System.getProperty("line.separator") +
                "detectedSurfaceNormalVector\t" +detectedPoint ==null ? "null" : detectedSurfaceNormalVector.toString()  + System.getProperty("line.separator") +
                '}';
    }
}
