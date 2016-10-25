function plotBoundaries2D( P , V , B, G, count, leader_count, step )

[P] = getSimulationStepData(step, P, count);
[B] = getSimulationStepData(step, B, count);

figure;

plot(P(~B,1),P(~B,2),'bo');
hold on
plot(P(B,1),P(B,2),'ro');
hold off
title('2D UAV position plot');
legend('Non boundary robots','Boundary robots','Location', 'best')
xlim([min(P(:,1))-5 max(P(:,1))+5]);
ylim([min(P(:,2))-5 max(P(:,2))+5]);
xlabel('x[m]');
ylabel('y[m]');
grid on;

end

