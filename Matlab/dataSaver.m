

dirname = 'stored\';
filename = 'record';
iter = 2;
filename = strcat(filename,num2str(iter));
path = strcat(dirname, filename); 
save(path, 'B', 'P', 'V', 'G', 'RG', 'count', 'leader_count',...
                    'simulationFrames','simulationStep');