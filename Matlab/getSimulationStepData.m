function [ M ] = getSimulationStepData( N , M, count )

allSteps = length(M)/count;

if(N>allSteps)
    disp('Simulation had not so many steps.')
end

M = M((N*count)+1 :1: (N*count)+count,:);

end

