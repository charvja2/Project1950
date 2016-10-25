

figure;
for simulationStep = 1 : 10 : simulationFrames
    plotConnectionsAnim(P , B, G, count, simulationStep);
    pause(0.01);
    if simulationStep ~= simulationFrames
        reset(gca);
    end
end
