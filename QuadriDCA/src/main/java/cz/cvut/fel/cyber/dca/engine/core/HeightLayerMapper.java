package cz.cvut.fel.cyber.dca.engine.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Jan on 16. 1. 2016.
 */
public class HeightLayerMapper {

    private Map<Quadracopter, HeightLayer> layerMapper;

    public HeightLayerMapper() {
        this.layerMapper = new HashMap<>();
    }

    public void update(List<Quadracopter> members, HeightProfile profile){
        if(profile.getLayers().isEmpty())return;

        layerMapper.clear();
        members.stream().forEach(member -> {
            if(member.isBoundary())layerMapper.put(member,  profile.getLayers().get(1));
                else layerMapper.put(member,  profile.getLayers().get(2));

        });

    }

    public Map<Quadracopter, HeightLayer> getLayerMapper() {
        return layerMapper;
    }
}
