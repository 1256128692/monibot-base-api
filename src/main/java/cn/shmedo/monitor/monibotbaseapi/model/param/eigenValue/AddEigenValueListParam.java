package cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataUnit;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
public class AddEigenValueListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "工程ID不能为空")
    private Integer projectID;

    @NotEmpty
    private List<AddEigenValueParam> dataList;


    @Override
    public ResultWrapper<?> validate() {
        validate(projectID, dataList);

        Date now = DateUtil.date();
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        dataList.forEach(e -> {
            e.setProjectID(projectID);
            e.setAllMonitorPoint(CollectionUtils.isEmpty(e.getMonitorPointIDList()));
            Optional.ofNullable(subject).ifPresent(s -> {
                e.setCreateUserID(s.getSubjectID());
                e.setUpdateUserID(s.getSubjectID());
            });
            e.setCreateTime(now);
            e.setUpdateTime(now);
        });

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    public static void validate(Integer projectID, List<AddEigenValueParam> dataList) {
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);

        List<Integer> mPointIdSet = dataList.stream()
                .filter(e -> !CollectionUtils.isEmpty(e.getMonitorPointIDList()))
                .flatMap(e -> e.getMonitorPointIDList().stream()).distinct().toList();
        if (!mPointIdSet.isEmpty()) {
            Assert.isTrue(mPointIdSet.size() == tbMonitorPointMapper
                    .selectCount(Wrappers.lambdaQuery(TbMonitorPoint.class)
                            .in(TbMonitorPoint::getID, mPointIdSet)
                            .eq(TbMonitorPoint::getEnable, true)), "监测点不存在或未启用");
        }

        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
        List<Integer> mItemIdSet = dataList.stream().map(AddEigenValueParam::getMonitorItemID).distinct().toList();
        Assert.isTrue(mItemIdSet.size() == tbMonitorItemMapper
                .selectCount(Wrappers.lambdaQuery(TbMonitorItem.class)
                        .in(TbMonitorItem::getID, mItemIdSet)
                        .eq(TbMonitorItem::getEnable, true)), "监测项目不存在或未启用");

        TbDataUnitMapper tbDataUnitMapper = ContextHolder.getBean(TbDataUnitMapper.class);
        List<Integer> mUnitIdSet = dataList.stream().map(AddEigenValueParam::getUnitID).distinct().toList();
        Assert.isTrue(mUnitIdSet.size() == tbDataUnitMapper
                .selectCount(Wrappers.lambdaQuery(TbDataUnit.class)
                        .in(TbDataUnit::getID, mUnitIdSet)), "数据单位不存在或未启用");


        TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper = ContextHolder.getBean(TbMonitorTypeFieldMapper.class);
        List<Integer> mFieldIdSet = dataList.stream().map(AddEigenValueParam::getMonitorTypeFieldID).distinct().toList();
        Assert.isTrue(mFieldIdSet.size() == tbMonitorTypeFieldMapper
                .selectCount(Wrappers.lambdaQuery(TbMonitorTypeField.class)
                        .in(TbMonitorTypeField::getID, mFieldIdSet)), "监测类型子字段不存在或未启用");

        TbEigenValueMapper tbEigenValueMapper = ContextHolder.getBean(TbEigenValueMapper.class);
        Assert.isFalse(tbEigenValueMapper.selectExist(projectID, null, dataList), "同一工程下的监测项目的监测点特征值名称已存在");
    }
}
