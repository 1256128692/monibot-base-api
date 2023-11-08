package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.FormPropertyType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertySubjectType;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.AddOtherDeviceBatchParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.QueryOtherDevicePageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.QueryOtherDeviceWithPropertyParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice.UpdateOtherDeviceParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice.TbOtherDevice4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice.TbOtherDeviceWithProperty;
import cn.shmedo.monitor.monibotbaseapi.service.IOtherDeviceService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.util.JsonUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final TbProjectInfoMapper tbProjectInfoMapper;
    private final TbPropertyModelMapper tbPropertyModelMapper;
    private final RedisService redisService;
    private final FileService fileService;

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
        tbProjectPropertyMapper.deleteProjectPropertyList(deviceIDList, PropertySubjectType.OtherDevice.getType());
    }

    @Override
    public PageUtil.Page<TbOtherDevice4Web> queryOtherDevicePage(QueryOtherDevicePageParam pa) {
        Page<TbOtherDevice4Web> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());
        IPage<TbOtherDevice4Web> pageData = tbOtherDeviceMapper.queryOtherDevicePage(page, pa);
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    @Override
    public TbOtherDeviceWithProperty queryOtherDeviceWithProperty(QueryOtherDeviceWithPropertyParam pa) {
        TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectById(pa.getTbOtherDevice().getProjectID());
        String location = null;
        String projectName = null;
        if (tbProjectInfo != null) {
            projectName = tbProjectInfo.getProjectName();
            if (ObjectUtil.isNotEmpty(tbProjectInfo.getLocation()) && JSONUtil.isTypeJSON(tbProjectInfo.getLocation())) {
                JSONObject jsonObject = JSONUtil.parseObj(tbProjectInfo.getLocation());
                Collection<Object> values = jsonObject.values();
                List<RegionArea> regionAreas = redisService.multiGet(RedisKeys.REGION_AREA_KEY, values, RegionArea.class);
                location = regionAreas.stream().map(RegionArea::getName).distinct().collect(Collectors.joining(StrUtil.EMPTY));

            }

        }
        if (pa.getTbOtherDevice().getTemplateID() == null) {
            TbOtherDeviceWithProperty obj = BeanUtil.copyProperties(pa.getTbOtherDevice(), TbOtherDeviceWithProperty.class);
            obj.setProjectLocation(location);
            obj.setProjectName(projectName);
            return obj;
        }
        TbPropertyModel tbPropertyModel = tbPropertyModelMapper.selectById(pa.getTbOtherDevice().getTemplateID());
        List<TbProperty> tbProperties = tbPropertyMapper.selectByModelIDs(List.of(pa.getTbOtherDevice().getTemplateID()));
        List<TbProjectProperty> tbProjectProperties = tbProjectPropertyMapper.selectList(
                new LambdaQueryWrapper<TbProjectProperty>()
                        .eq(TbProjectProperty::getProjectID, pa.getTbOtherDevice().getID())
                        .eq(TbProjectProperty::getSubjectType, PropertySubjectType.OtherDevice.getType())
        );
        TbOtherDeviceWithProperty obj = TbOtherDeviceWithProperty.valueOf(pa.getTbOtherDevice(), tbProperties, tbProjectProperties, location, projectName,
                ObjectUtil.isNull(tbPropertyModel) ? null : tbPropertyModel.getName()
        );

        // 处理为文件或者图片的属性
        obj.getPropertyList().forEach(e -> {
            if (FormPropertyType.FILE.getCode().equals(e.getType().intValue())
                    || FormPropertyType.PICTURE.getCode().equals(e.getType().intValue())) {
                e.setOssList(
                        Arrays.stream(e.getValue().split(",")).toList()
                );
            }
        });
        List<String> ossAllList = obj.getPropertyList().stream()
                .filter(e -> ObjectUtil.isNotEmpty(e.getOssList()))
                .flatMap(
                e -> e.getOssList().stream()
        ).toList();
        if (ObjectUtil.isNotEmpty(ossAllList)) {
            List<FileInfoResponse> fileUrlList = fileService.getFileUrlList(ossAllList, pa.getCompanyID());
            Map<String, FileInfoResponse> fileMap = fileUrlList.stream().collect(Collectors.toMap(
                    FileInfoResponse::getFilePath, Function.identity()
            ));
            obj.getPropertyList().forEach(
                    e -> {
                        if (ObjectUtil.isNotEmpty(e.getOssList())) {
                            e.setFileList(
                                    e.getOssList().stream().map(
                                            fileMap::get
                                    ).toList());
                        }
                    }
            );
        }
        return obj;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOtherDevice(UpdateOtherDeviceParam pa, Integer subjectID) {
        baseMapper.updateById(pa.update(subjectID));
        if (pa.getPropertyList() != null) {
            tbProjectPropertyMapper.deleteProjectPropertyList(List.of(pa.getID()), PropertySubjectType.OtherDevice.getType());
            tbProjectPropertyMapper.insertBatch(pa.toProjectPropertyList());
        }
    }
}
