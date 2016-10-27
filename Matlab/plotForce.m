function plotForce( P , B, G,  F, count, step )

FlockForce = F(1:7:end,:);
BoundaryForce = F(2:7:end,:);
LeaderFollowForce = F(3:7:end,:);
ThicknessForce = F(4:7:end,:);
DensityForce = F(5:7:end,:);
HeightForce = F(6:7:end,:);
SumForce = F(7:7:end,:);

[P] = getSimulationStepData(step, P, count);
[B] = getSimulationStepData(step, B, count);

[FlockForce] = getSimulationStepData(step, FlockForce, count);
[BoundaryForce] = getSimulationStepData(step, BoundaryForce, count);
[LeaderFollowForce] = getSimulationStepData(step, LeaderFollowForce, count);
[ThicknessForce] = getSimulationStepData(step, ThicknessForce, count);
[DensityForce] = getSimulationStepData(step, DensityForce, count);
[HeightForce] = getSimulationStepData(step, HeightForce, count);
[SumForce] = getSimulationStepData(step, SumForce, count);

forcesToPlot = [1 1 0 0 0 0 0];

figure;


plot3(P(~B,1),P(~B,2),P(~B,3),'bo','MarkerSize',20);
hold on

plot3(P(B,1),P(B,2),P(B,3),'ro','MarkerSize',20);
hold on
title(['UAV 3D position force plot ' num2str(step)]);
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
        if(forcesToPlot(1,2)==1)
            plotArrow3D(P(idx,:),BoundaryForce(idx,:),'g-');
        end
    end
    if(forcesToPlot(1,1)==1)
        plotArrow3D(P(idx,:),FlockForce(idx,:),'k-');
    end
    if(forcesToPlot(1,3)==1)
        plotArrow3D(P(idx,:),LeaderFollowForce(idx,:),'m-');
    end
    if(forcesToPlot(1,5)==1)
        plotArrow3D(P(idx,:),DensityForce(idx,:),'g-');
    end
    if(forcesToPlot(1,4)==1)
        plotArrow3D(P(idx,:),ThicknessForce(idx,:),'y-');
    end
    if(forcesToPlot(1,6)==1)
        plotArrow3D(P(idx,:),HeightForce(idx,:),'b-');
    end
    if(forcesToPlot(1,7)==1)
        plotArrow3D(P(idx,:),SumForce(idx,:),'c-');
    end
    hold on
    
end
%hold off


if all(B)
    legendCell = cell(1,2);
    legendCell{1} = 'Boundary robots'; 
    legendCell{2} = 'Boundary edges';
else
    legendCell = cell(1,4);
    legendCell{1} = 'Boundary robots'; 
    legendCell{2} = 'Non-boundary robots';
    legendCell{3} = 'Boundary edges'; 
    legendCell{4} = 'Non-boundary edges';
end

labels = cell(size(forcesToPlot));
labels{1} = 'Boundary tension force';
labels{2} = 'Flock force';
labels{3} = 'Leader follow force';
labels{4} = 'Thickness force';
labels{5} = 'Density force';
labels{6} = 'Height force';
labels{7} = 'Sum force';

labels = labels(logical(forcesToPlot));

legend([legendCell labels]);

xlim([min(P(:,1))-5 max(P(:,1))+5]);
ylim([min(P(:,2))-5 max(P(:,2))+5]);
zlim([0 max(P(:,3))+5]);
xlabel('x[m]');
ylabel('y[m]');
zlabel('z[m]');
grid on;


end

