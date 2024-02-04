package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 16:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryThresholdBaseConfigParam extends QueryMonitorWithThresholdConfigCountParam {

    @NotNull(message = "监测项目ID不能为空")
    @Positive(message = "监测项目ID必须为正值")
    private Integer monitorItemID;
    @JsonIgnore
    private Integer companyID;
    @JsonIgnore
    private Integer monitorType;

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        Integer projectID = getProjectID();
        List<TbProjectInfo> tbProjectInfoList = ContextHolder.getBean(TbProjectInfoMapper.class)
                .selectList(new LambdaQueryWrapper<TbProjectInfo>().eq(TbProjectInfo::getID, projectID));
        if (CollUtil.isEmpty(tbProjectInfoList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程不存在");
        }
        List<TbMonitorItem> tbMonitorItemList = ContextHolder.getBean(TbMonitorItemMapper.class)
                .selectList(new LambdaQueryWrapper<TbMonitorItem>().eq(TbMonitorItem::getID, monitorItemID)
                        .eq(TbMonitorItem::getProjectID, projectID));
        if (CollUtil.isEmpty(tbMonitorItemList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
        }
        this.companyID = tbProjectInfoList.stream().findAny().map(TbProjectInfo::getCompanyID).orElseThrow();
        this.monitorType = tbMonitorItemList.stream().findAny().map(TbMonitorItem::getMonitorType).orElseThrow();
        return null;
    }
}
