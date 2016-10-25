function plotLeaders2D( P ,  count, leader_count, step )

[P] = getSimulationStepData(step, P, count);


figure;

plot(P(leader_count+1:end,1),P(leader_count+1:end,2),'bo');
hold on
plot(P(1:leader_count,1),P(1:leader_count,2),'ro');
hold off

xlim([min(P(:,1))-5 max(P(:,1))+5]);
ylim([min(P(:,2))-5 max(P(:,2))+5]);
xlabel('x[m]');
ylabel('y[m]');
grid on;


end

