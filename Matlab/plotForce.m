function plotForce( P , B, G,  F, count, step )

FlockForce = F(1:6:end,:);
BoundaryForce = F(2:6:end,:);
LeaderFollowForce = F(3:6:end,:);
ThicknessForce = F(4:6:end,:);
DensityForce = F(5:6:end,:);
SumForce = F(1:6:end,:);

[P] = getSimulationStepData(step, P, count);
[B] = getSimulationStepData(step, B, count);
[FlockForce] = getSimulationStepData(step, FlockForce, count);
[BoundaryForce] = getSimulationStepData(step, BoundaryForce, count);
[LeaderFollowForce] = getSimulationStepData(step, LeaderFollowForce, count);
[ThicknessForce] = getSimulationStepData(step, ThicknessForce, count);
[DensityForce] = getSimulationStepData(step, DensityForce, count);
[SumForce] = getSimulationStepData(step, SumForce, count);


figure;

plot3(P(~B,1),P(~B,2),P(~B,3),'bo','MarkerSize',20);
hold on
plot3(P(B,1),P(B,2),P(B,3),'ro','MarkerSize',20);
hold on
title(['UAV 3D position with boundary force' num2str(step)]);
hold on
for idx = 1:count
    pos1 = P(idx,:);
    pos2 = P(G(idx,:)'&(~B(:,1)),:);
    
    pos1 = repmat(pos1,[size(pos2,1) 1]);
    pos3 = zeros(size(pos1,1)+size(pos2,1),3);
    pos3(1:2:end,:) = pos1;
    pos3(2:2:end,:) = pos2;
  
    plot3(pos3(:,1),pos3(:,2),pos3(:,3),'b-');
    
    if B(idx)    
        pos1 = P(idx,:);
        pos2 = P(G(idx,:)'&(B(:,1)),:);

        pos1 = repmat(pos1,[size(pos2,1) 1]);
        pos3 = zeros(size(pos1,1)+size(pos2,1),3);
        pos3(1:2:end,:) = pos1;
        pos3(2:2:end,:) = pos2;
        plot3(pos3(:,1),pos3(:,2),pos3(:,3),'r-');
        plotArrow3D(P(idx,:),BoundaryForce(idx,:),'g-');

    end
    
    plotArrow3D(P(idx,:),FlockForce(idx,:),'k-');
%     plotArrow3D(P(idx,:),SumForce(idx,:),'m-');


    hold on
    
end
%hold off

legend('Non boundary robots','Boundary robots','Non boundary edges',...
    'Boundary edges', 'Boundary force','Flock force','Location', 'best')

xlim([min(P(:,1))-5 max(P(:,1))+5]);
ylim([min(P(:,2))-5 max(P(:,2))+5]);
zlim([0 max(P(:,3))+5]);
xlabel('x[m]');
ylabel('y[m]');
zlabel('z[m]');
grid on;


end

