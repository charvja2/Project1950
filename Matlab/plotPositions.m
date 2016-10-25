function plotPositions( P , counts , step)
% Function plots position of UAVs in time

P = getSimulationStepData(step, P,  count);


figure;
plot3(P(1:count,1),P(1:count,2),P(1:count,3),'bo');

title('3D UAV position plot');
xlim([min(P(:,1))-5 max(P(:,1))+5]);
ylim([min(P(:,2))-5 max(P(:,2))+5]);
zlim([0 max(P(:,3))+5]);
xlabel('x[m]');
ylabel('y[m]');
zlabel('z[m]');
grid on;

end

