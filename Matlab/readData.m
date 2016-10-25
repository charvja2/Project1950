function [ P , V , B, F, G, RG, count, leader_count ] = readData()
    % Function reads experiment data from different files
    % P .. vec of positions
    % V .. vec of velocities
    % B .. logical vec of bounary robots
    % G .. connection graph
    % RG .. reduced connection graph
    % count .. count of robots
    % leader_count .. count of leaders

    dirPath = 'C:\Users\Jan\Desktop\FEL\7 sem\BAP\Projects\Project1950\FlightRecords\';
    info = load([dirPath 'experimentRecord.txt']);
    count = info(1,1);
    leader_count = info(2,1);
    
    P = load([dirPath 'positionRecord.txt']);
    V = P(2:2:end,:);
    P(2:2:end,:) = [];
    
    F = load([dirPath 'forceRecord.txt']);
    
    B = logical(load([dirPath 'boundaryRecord.txt']));
    G = logical(load([dirPath 'connectionRecord.txt']));
    RG = G(2:2:end,:);
    G(2:2:end,:) = [];
    
    % skip first few initial records
    
    few = 3;
    
    B = B(few*count+1:end,1);
    P = P(few*count+1:end,:);
    V = V(few*count+1:end,:);
    G = G(few*count+1:end,:);
        
    if(mod(size(B,1),count)~=0)
        disp('Wrong file size.');
    end
    if(mod(size(P,1),count)~=0)
        disp('Wrong file size.');
    end
    if(mod(size(V,1),count)~=0)
        disp('Wrong file size.');
    end
    if(mod(size(G,1),count)~=0)
        disp('Wrong file size.');
    end
    if(mod(size(RG,1),count)~=0)
        disp('Wrong file size.');
    end
end

