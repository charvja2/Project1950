package cz.cvut.fel.cyber.dca.engine.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jan on 16. 1. 2016.
 */
public class HeightLayerMapper {

    private Map<Quadrotor, HeightLayer> layerMapper;

    public HeightLayerMapper() {
        this.layerMapper = new HashMap<>();
    }

    public void update(List<Quadrotor> members, HeightProfile profile){
        if(profile.getLayers().isEmpty())return;

        layerMapper.clear();
        members.stream().forEach(member -> {
            RobotGroup.getGroupHeightProfile().getLayers().stream().forEach(layer ->{
                if(layer.inRange(member.getPosition().getZ()))layerMapper.put(member,layer);
            });
        });

    }

    public Map<Quadrotor, HeightLayer> getLayerMapper() {
        return layerMapper;
    }
}
