function plotConnectionsAnim( P , B, G, count, step )

[P] = getSimulationStepData(step, P, count);
[B] = getSimulationStepData(step, B, count);
[G] = getSimulationStepData(step, G, count);

plot3(P(~B,1),P(~B,2),P(~B,3),'bo','MarkerSize',20);
hold on
plot3(P(B,1),P(B,2),P(B,3),'ro','MarkerSize',20);
hold on
title(['UAV 3D position with connections' num2str(step)]);
legend('Non boundary robots','Boundary robots','Location', 'best')
hold on
for idx = 1:count
    pos1 = P(idx,:);
    pos2 = P(G(idx,:)',:);
    
    pos1 = repmat(pos1,[size(pos2,1) 1]);
    pos3 = zeros(size(pos1,1)+size(pos2,1),3);
    pos3(1:2:end,:) = pos1;
    pos3(2:2:end,:) = pos2;
  
    plot3(pos3(:,1),pos3(:,2),pos3(:,3),'m-');
end

xlim([min(P(:,1))-5 max(P(:,1))+5]);
ylim([min(P(:,2))-5 max(P(:,2))+5]);
zlim([0 max(P(:,3))+5]);
xlabel('x[m]');
ylabel('y[m]');
zlabel('z[m]');
grid on;



end