package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbOtherDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.AddOtherDeviceBatchParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.QueryOtherDevicePageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.QueryOtherDeviceWithPropertyParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.UpdateOtherDeviceParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice.TbOtherDevice4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice.TbOtherDeviceWithProperty;
import cn.shmedo.monitor.monibotbaseapi.service.IOtherDeviceService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-27 17:50
 **/
@Service
@AllArgsConstructor
public class OtherDeviceServiceImpl extends ServiceImpl<TbOtherDeviceMapper, TbOtherDevice> implements IOtherDeviceService {
    private final TbOtherDeviceMapper tbOtherDeviceMapper;
    private final TbProjectPropertyMapper tbProjectPropertyMapper;
    private final TbPropertyMapper tbPropertyMapper;
    private final RedisService redisService;
    private final TbProjectInfoMapper tbProjectInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOtherDeviceBatch(AddOtherDeviceBatchParam pa, Integer subjectID) {
        baseMapper.insertBatchSomeColumn(pa.toList(subjectID));
        if (pa.getTemplateID() != null) {
            tbProjectPropertyMapper.insertBatch(pa.toProjectPropertyList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOtherDevice(List<Integer> deviceIDList) {
        baseMapper.deleteBatchIds(deviceIDList);
        tbProjectPropertyMapper.deleteProjectPropertyList(deviceIDList);
    }

    @Override
    public PageUtil.Page<TbOtherDevice4Web> queryOtherDevicePage(QueryOtherDevicePageParam pa) {
        Page<TbOtherDevice4Web> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());
        IPage<TbOtherDevice4Web> pageData = tbOtherDeviceMapper.queryOtherDevicePage(page, pa);
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    @Override
    public TbOtherDeviceWithProperty queryOtherDeviceWithProperty(QueryOtherDeviceWithPropertyParam pa) {
        if (pa.getTbOtherDevice().getTemplateID() == null) {
            return BeanUtil.copyProperties(pa.getTbOtherDevice(), TbOtherDeviceWithProperty.class);
        }
        List<TbProperty> tbProperties = tbPropertyMapper.selectByModelIDs(List.of(pa.getTbOtherDevice().getTemplateID()));
        List<TbProjectProperty> tbProjectProperties = tbProjectPropertyMapper.selectList(
                new LambdaQueryWrapper<TbProjectProperty>()
                        .eq(TbProjectProperty::getProjectID, pa.getTbOtherDevice().getID())
        );
        TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectById(pa.getTbOtherDevice().getProjectID());
        String location = null;
        if (tbProjectInfo != null && ObjectUtil.isNotEmpty(tbProjectInfo.getLocation())) {
            RegionArea area = redisService.get(RedisKeys.REGION_AREA_KEY,
                    BeanUtil.copyProperties(tbProjectInfo, ProjectInfo.class).getLocationInfo()
                    , RegionArea.class);
            location = area != null ? area.getName() : StrUtil.EMPTY;
        }

        return TbOtherDeviceWithProperty.valueOf(pa.getTbOtherDevice(), tbProperties, tbProjectProperties, location);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOtherDevice(UpdateOtherDeviceParam pa, Integer subjectID) {
        baseMapper.updateById(pa.update(subjectID));
        if (pa.getPropertyList() != null) {
            tbProjectPropertyMapper.deleteProjectPropertyList(List.of(pa.getID()));
            tbProjectPropertyMapper.insertBatch(pa.toProjectPropertyList());
        }
    }
}
