function plotBoundaries( P , B, G, count, step )

[P] = getSimulationStepData(step, P, count);
[B] = getSimulationStepData(step, B, count);

figure;
plot3(P(~B,1),P(~B,2),P(~B,3),'bo');
hold on
plot3(P(B,1),P(B,2),P(B,3),'ro');
hold off
title('3D UAV position plot');

legend('Non boundary robots','Boundary robots','Location', 'best')
xlim([min(P(:,1))-5 max(P(:,1))+5]);
ylim([min(P(:,2))-5 max(P(:,2))+5]);
zlim([0 max(P(:,3))+5]);
xlabel('x[m]');
ylabel('y[m]');
zlabel('z[m]');
grid on;


end



