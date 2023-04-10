package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbParameterMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.SensorConfigField;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataSourceComposeType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DatasourceType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorTypeFieldClass;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ParamSubjectType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 新增传感器请求体
 *
 * @author Chengfs on 2023/3/31
 */
@Data
public class SaveSensorRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    /**
     * 传感器图片路径
     */
    private String imagePath;

    /**
     * 传感器别名
     */
    private String alias;

    /**
     * 监测类型
     */
    @NotNull(message = "监测类型不能为空")
    private Integer monitorType;

    /**
     * 数据来源类型, 默认为1 <br/>
     * 1单一物模型单一传感器 <br/>
     * 2多个物联网传感器（同一物模型多个或者不同物模型多个）<br/>
     * 3物联网传感器+监测传感器<br/>
     * 4单个监测传感器<br/>
     * 5多个监测传感器<br/>
     * 100API 推送
     */
    @NotNull(message = "数据来源类型不能为空")
    private DataSourceComposeType dataSourceComposeType = DataSourceComposeType.SINGLE_IOT;

    /**
     * 监测类型模板分布式唯一ID
     */
    @NotNull(message = "监测类型模板分布式唯一ID不能为空")
    private String templateDataSourceID;

    /**
     * 监测类型模板ID
     */
    @NotNull(message = "监测类型模板ID不能为空")
    private Integer templateID;

    /**
     * 数据源列表
     */
    @NotEmpty(message = "数据源列表不能为空")
    private List<DataSource> dataSourceList;

    /**
     * 扩展配置列表
     */
    private List<SensorConfigField> exFields;

    @JsonIgnore
    private String configFieldValue;

    /**
     * 参数列表
     */
    private List<SensorConfigField> paramFields;

    @JsonIgnore
    private List<TbParameter> parameterList = Collections.emptyList();


    @Data
    public static class DataSource {

        /**
         * 数据源类型 1-物联网传感器 2-监测传感器
         */
        @NotNull(message = "数据源类型不能为空")
        private DatasourceType dataSourceType;

        /**
         * 模板数据源标识
         */
        @NotNull(message = "模板数据源标识不能为空")
        private String templateDataSourceToken;

        /**
         * (监测/物联网)传感器名称
         */
        @NotNull(message = "传感器名称不能为空")
        private String sensorName;

        /**
         * 设备传感器标识 数据源类型为1时必填
         */
        private String uniqueToken;

        /**
         * 扩展配置
         */
        private String exValues;

        public void setDataSourceType(Integer dataSourceType) {
            this.dataSourceType = DatasourceType.codeOf(dataSourceType);
        }
    }


    @Override
    public ResultWrapper<?> validate() {
        if (CollUtil.isNotEmpty(dataSourceList)) {
            dataSourceList.forEach(e -> {
                Assert.notNull(e.getDataSourceType(), "数据源类型不能为空1");
                if (e.getDataSourceType() == DatasourceType.IOT) {
                    Assert.notBlank(e.getUniqueToken(), "设备传感器标识不能为空");
                }
            });
        }
        //校验扩展配置
        if (CollUtil.isNotEmpty(exFields)) {
            TbMonitorTypeFieldMapper monitorTypeFieldMapper = SpringUtil.getBean(TbMonitorTypeFieldMapper.class);
            Map<Integer, String> exMap = monitorTypeFieldMapper.selectList(new LambdaQueryWrapper<TbMonitorTypeField>()
                    .eq(TbMonitorTypeField::getMonitorType, monitorType)
                    .eq(TbMonitorTypeField::getFieldClass, MonitorTypeFieldClass.ExtendedConfigurations.getFieldClass())
                    .in(TbMonitorTypeField::getID, exFields.stream().map(SensorConfigField::getId).toList())
                    .select(TbMonitorTypeField::getID, TbMonitorTypeField::getFieldName)
            ).stream().collect(Collectors.toMap(TbMonitorTypeField::getID, TbMonitorTypeField::getFieldName));
            exFields.forEach(ex -> {
                Assert.isTrue(exMap.containsKey(ex.getId()), "扩展配置项 [" + ex.getId() + "]不存在");
                ex.setName(exMap.get(ex.getId()));
            });
            Map<String, String> exConfig = exFields.stream()
                    .map(e -> Map.entry(e.getName(), e.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            this.configFieldValue = JSONUtil.toJsonStr(exConfig);
            exFields.clear();
            exMap.clear();
        }

        //校验参数
        if (paramFields.isEmpty()) {
            TbParameterMapper parameterMapper = SpringUtil.getBean(TbParameterMapper.class);
            Map<Integer, TbParameter> paramMap = parameterMapper.selectList(new LambdaQueryWrapper<TbParameter>()
                    .eq(TbParameter::getSubjectType, ParamSubjectType.Template.getType())
                    .eq(TbParameter::getSubjectID, templateID)
            ).stream().collect(Collectors.toMap(TbParameter::getID, e -> e));

            parameterList = paramFields.stream().map(e -> {
                Assert.isTrue(paramMap.containsKey(e.getId()), "参数配置项 [" + e.getId() + "]不存在");
                TbParameter param = paramMap.get(e.getId());
                param.setID(null);
                param.setSubjectID(null);
                param.setSubjectType(ParamSubjectType.Sensor.getType());
                return param;
            }).toList();
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    