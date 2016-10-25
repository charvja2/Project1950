function plotLeaders( P , count, leader_count, step )

[P] = getSimulationStepData(step, P,  count);

figure;

plot3(P(leader_count+1:end,1),...
    P(leader_count+1:end,2),P(leader_count+1:end,3),'bo');
hold on
plot3(P(1:leader_count,1),P(1:leader_count,2),P(1:leader_count,3),'ro');
hold off

xlim([min(P(:,1))-5 max(P(:,1))+5]);
ylim([min(P(:,2))-5 max(P(:,2))+5]);
zlim([0 max(P(:,3))+5]);
xlabel('x[m]');
ylabel('y[m]');
zlabel('z[m]');
grid on;



end

