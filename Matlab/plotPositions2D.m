function plotPositions2D( P , count, step)
% Function plots position of UAVs in time

[P] = getSimulationStepData(step, P, count);

figure;
plot(P(1:count,1),P(1:count,2),'bo');

title('2D UAV position plot');
xlim([min(P(1:count,1))-5 max(P(1:count,1))+5]);
ylim([min(P(1:count,2))-5 max(P(1:count,2))+5]);
xlabel('x[m]');
ylabel('y[m]');
grid on;


end

