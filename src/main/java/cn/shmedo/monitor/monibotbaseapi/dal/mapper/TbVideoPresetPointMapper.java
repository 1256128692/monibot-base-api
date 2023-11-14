package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoPresetPoint;
import cn.shmedo.monitor.monibotbaseapi.model.response.presetPoint.PresetPointWithDeviceInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbVideoPresetPointMapper extends BaseMapper<TbVideoPresetPoint> {
    List<PresetPointWithDeviceInfo> selectPresetPointWithDeviceInfo(List<Integer> presetPointIDList);
}