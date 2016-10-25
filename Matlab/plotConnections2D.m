function plotConnections2D( P , B, G, count, leader_count, step )

[P] = getSimulationStepData(step, P, count);
[B] = getSimulationStepData(step, B, count);
[G] = getSimulationStepData(step, G, count);

figure;

plot(P(~B,1),P(~B,2),'bo','MarkerSize',20);
hold on
plot(P(B,1),P(B,2),'ro','MarkerSize',20);
hold on
title('UAV 2D position with connections');
legend('Non boundary robots','Boundary robots','Location', 'best')

hold on
for idx = 1:count
    pos1 = P(idx,:);
    pos2 = P(G(idx,:)',:);
    
    pos1 = repmat(pos1,[size(pos2,1) 1]);
    pos3 = zeros(size(pos1,1)+size(pos2,1),3);
    pos3(1:2:end,:) = pos1;
    pos3(2:2:end,:) = pos2;
  
    plot(pos3(:,1),pos3(:,2),'m-');
    
end

hold off

xlim([min(P(:,1))-5 max(P(:,1))+5]);
ylim([min(P(:,2))-5 max(P(:,2))+5]);
xlabel('x[m]');
ylabel('y[m]');
grid on;
