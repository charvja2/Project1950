% script loades files from several different files containing 
% simulation records

[ P , V , B, F, G, RG, count, leader_count ] = readData(  );
simulationFrames = length(B)/count;
simulationStep = 1000;