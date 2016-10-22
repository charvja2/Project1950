package cz.cvut.fel.cyber.dca.algorithms;

import cz.cvut.fel.cyber.dca.engine.core.Loopable;
import cz.cvut.fel.cyber.dca.engine.core.Quadrotor;
import cz.cvut.fel.cyber.dca.engine.core.Unit;
import cz.cvut.fel.cyber.dca.engine.data.ThicknessDeterminationData;
import cz.cvut.fel.cyber.dca.engine.util.Vector3;
import javafx.util.Pair;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.cvut.fel.cyber.dca.engine.experiment.Experiment.DIMENSION;

/**
 * Created by Jan on 28. 10. 2015.
 */
public class ThicknessDeterminationNContractionAlgorithm implements Loopable<Quadrotor,Vector3> {

    private int lambda = 2;
    private double param = 1;

    public double getParam() {
        return param;
    }

    public void setParam(double param) {
        this.param = param;
    }

    private void handleBoundaryHopDistance(Quadrotor input){
        if(input.isBoundary())input.getThicknessDeterminationData().setB(0);
        else if(input.getReceivedStabilityImprovementData().stream().mapToInt(pair -> pair.getValue().getB()).min().isPresent()){
            int min = input.getReceivedStabilityImprovementData().stream().mapToInt(pair -> pair.getValue().getB()).min().getAsInt();
            Pair<Quadrotor,ThicknessDeterminationData> pairMinB = input.getReceivedStabilityImprovementData().stream().filter(pair -> pair.getValue().getB()==min).findAny().get();
            input.getThicknessDeterminationData().setB(pairMinB.getValue().getB() + 1);
        }
    }

    private void handleThickness(Quadrotor input){
        int max = 0;

        if (input.getReducedNeighbors().stream().filter(neighbor -> neighbor.getThicknessDeterminationData().getT() + lambda > neighbor.getThicknessDeterminationData().getH())
                .mapToInt(neighbor -> neighbor.getThicknessDeterminationData().getT()).max().isPresent()){
            max = input.getReducedNeighbors().stream().filter(neighbor -> neighbor.getThicknessDeterminationData().getT() + lambda > neighbor.getThicknessDeterminationData().getH())
                    .mapToInt(neighbor -> neighbor.getThicknessDeterminationData().getT()).max().getAsInt();
        }
        max = Math.max(input.getThicknessDeterminationData().getB(),max);

        input.getThicknessDeterminationData().setT(max);
    }

    private void handleCircleHopDistance(Quadrotor input){
        if(input.getThicknessDeterminationData().getB()==input.getThicknessDeterminationData().getT())
            input.getThicknessDeterminationData().setH(0);
        else{
            OptionalInt min = input.getReducedNeighbors().stream().filter(neighbor
                    -> neighbor.getThicknessDeterminationData().getT() == input.getThicknessDeterminationData().getT())
                    .mapToInt(neighbor -> neighbor.getThicknessDeterminationData().getH()+1).min();
            if(min.isPresent())input.getThicknessDeterminationData().setH(min.getAsInt());
        }
    }

    private Vector3 computeThicknessContractionForce(Quadrotor unit){
        final Vector3 thicknessForce = new Vector3();
        int thickness = unit.getThicknessDeterminationData().getT();
        int centerHopDistance = unit.getThicknessDeterminationData().getH();

        if(unit.getReducedNeighbors().size() >= 2 ){
                Set<Quadrotor> reducedNeighbors = unit.getReducedNeighbors();

                reducedNeighbors = reducedNeighbors.stream().sorted((a,b) -> {
                    if (a.getThicknessDeterminationData().getH() < b.getThicknessDeterminationData().getH())return 1;
                    else return -1;
                }).collect(Collectors.toSet());

                reducedNeighbors.stream().forEach(neighbor ->{
                    Vector3 increment = unit.getRelativeLocalization(neighbor).newUnitVector();
                    increment.timesScalar(1/1+neighbor.getThicknessDeterminationData().getH());
                    thicknessForce.plus(increment);
                });
        }

        thicknessForce.timesScalar(thickness);
        return thicknessForce;

    }

    @Override
    public Vector3 loop(Quadrotor input) {
        handleBoundaryHopDistance(input);
        handleThickness(input);
        handleCircleHopDistance(input);

        if (((input.getThicknessDeterminationData().getT()==0)&&(input.getThicknessDeterminationData().getH()==0)&&(input.getThicknessDeterminationData().getB()==0))
            &&(input.isBoundary()))
            input.setTailRobot(true);
        else
            input.setTailRobot(false);

        Vector3 force = computeThicknessContractionForce(input);
        force.timesScalar(param);
        if(DIMENSION == 2)force.setZ(0);

        return force;
    }
}
