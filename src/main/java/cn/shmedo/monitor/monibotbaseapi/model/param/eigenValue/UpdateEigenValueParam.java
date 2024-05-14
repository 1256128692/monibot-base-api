package cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ScopeType;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
public class UpdateEigenValueParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "特征ID不能为空")
    private Integer eigenValueID;

    @Positive
    @NotNull(message = "工程ID不能为空")
    private Integer projectID;

    @NotNull(message = "作用域scope不能为空")
    private ScopeType scope;

    @Positive
    @NotNull(message = "监测项目ID不能为空")
    private Integer monitorItemID;

    @Valid
    private List<@Positive @NotNull Integer> monitorPointIDList;

    @NotNull(message = "监测类型子类型monitorTypeFieldID不能为空")
    private Integer monitorTypeFieldID;

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotNull(message = "值不能为空")
    private Double value;

    @Positive
    @NotNull(message = "单位ID不能为空")
    private Integer unitID;

    private String exValue;

    public static TbEigenValue toNewVo(UpdateEigenValueParam pa, Integer subjectID) {
        DateTime date = DateUtil.date();
        TbEigenValue vo = new TbEigenValue();
        vo.setId(pa.getEigenValueID());
        vo.setValue(pa.getValue());
        vo.setExValue(pa.getExValue());
        vo.setName(pa.getName());
        vo.setScope(pa.getScope());
        vo.setUpdateTime(date);
        vo.setMonitorItemID(pa.getMonitorItemID());
        vo.setMonitorTypeFieldID(pa.getMonitorTypeFieldID());
        vo.setUnitID(pa.getUnitID());
        vo.setProjectID(pa.getProjectID());
        vo.setUpdateUserID(subjectID);
        vo.setAllMonitorPoint(CollectionUtils.isEmpty(pa.getMonitorPointIDList()));
        return vo;
    }


    @Override
    public ResultWrapper<?> validate() {
        TbEigenValueMapper mapper = ContextHolder.getBean(TbEigenValueMapper.class);
        TbEigenValue entity = mapper.selectById(eigenValueID);
        Assert.notNull(entity, "特征值不存在");
        Assert.isTrue(entity.getProjectID().equals(projectID), "不能改变特征值所属项目");

        if (!CollectionUtils.isEmpty(monitorPointIDList)) {
            TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
            Assert.isTrue(tbMonitorPointMapper
                    .exists(Wrappers.<TbMonitorPoint>lambdaQuery()
                            .in(TbMonitorPoint::getID, this.monitorPointIDList)
                            .eq(TbMonitorPoint::getEnable, true)), "监测点不存在或未启用");
        }

        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
        Assert.isTrue(tbMonitorItemMapper.exists(Wrappers.<TbMonitorItem>lambdaQuery()
                .eq(TbMonitorItem::getID, this.monitorItemID)
                .eq(TbMonitorItem::getEnable, true)), "监测项目不存在或未启用");

        TbDataUnitMapper tbDataUnitMapper = ContextHolder.getBean(TbDataUnitMapper.class);
        Assert.isTrue(tbDataUnitMapper.exists(Wrappers.<TbDataUnit>lambdaQuery()
                .eq(TbDataUnit::getID, this.unitID)), "数据单位不存在或未启用");


        TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper = ContextHolder.getBean(TbMonitorTypeFieldMapper.class);
        Assert.isTrue(tbMonitorTypeFieldMapper.exists(Wrappers.<TbMonitorTypeField>lambdaQuery()
                .eq(TbMonitorTypeField::getID, this.monitorTypeFieldID)), "监测类型子字段不存在或未启用");

        //校验名称是否重复
        TbEigenValueMapper tbEigenValueMapper = ContextHolder.getBean(TbEigenValueMapper.class);
        AddEigenValueParam param = new AddEigenValueParam();
        param.setName(this.name);
        param.setScope(this.scope);
        param.setMonitorItemID(this.projectID);
        param.setMonitorPointIDList(this.monitorPointIDList);
        Assert.isFalse(tbEigenValueMapper.selectExist(projectID, eigenValueID,
                List.of(param)), "同一工程下的监测项目的监测点特征值名称已存在");

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

}
