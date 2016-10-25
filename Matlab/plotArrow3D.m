function plotArrow3D( starts,ends, style)

quiver3(starts(:,1), starts(:,2), starts(:,3),...
                            ends(:,1), ends(:,2), ends(:,3),style)
axis equal

end

