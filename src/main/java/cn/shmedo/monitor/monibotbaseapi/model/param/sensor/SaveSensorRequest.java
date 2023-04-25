package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.monitor.enums.DataSourceComposeType;
import cn.shmedo.iot.entity.api.monitor.enums.DataSourceType;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.monitor.enums.ParameterSubjectType;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.SensorConfigField;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @NotEmpty(message = "传感器名称不能为空")
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
    private DataSourceComposeType dataSourceComposeType = DataSourceComposeType.SINGLE_IOT;

    /**
     * 监测类型模板ID
     */
    private Integer templateID;

    /**
     * 数据源列表
     */
    private List<DataSource> dataSourceList;

    /**
     * 扩展配置列表
     */
    private List<SensorConfigField> exFields;

    /**
     * 参数列表
     */
    private List<SensorConfigField> paramFields;

    @JsonIgnore
    private List<TbParameter> parameterList = Collections.emptyList();

    @JsonIgnore
    private String configFieldValue;


    @Data
    public static class DataSource {

        /**
         * 数据源类型 1-物联网传感器 2-监测传感器
         */
        @NotNull(message = "数据源类型不能为空")
        private DataSourceType dataSourceType;

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
            this.dataSourceType = DataSourceType.codeOf(dataSourceType);
        }
    }


    @Override
    public ResultWrapper<?> validate() {
        //校验名称
        TbSensorMapper sensorMapper = SpringUtil.getBean(TbSensorMapper.class);
        Long count = sensorMapper.selectCount(new LambdaQueryWrapper<TbSensor>()
                .eq(TbSensor::getProjectID, projectID)
                .eq(TbSensor::getAlias, alias));
        Assert.isTrue(count == 0, "传感器名称已存在");

        //校验数据源
        if (dataSourceComposeType != DataSourceComposeType.API) {
            Assert.notNull(templateID, "监测类型模板ID不能为空");
            Assert.notEmpty(dataSourceList, "数据源列表不能为空");
        } else {
            templateID = -1;
            dataSourceList = Collections.emptyList();
        }
        dataSourceList.forEach(e -> {
            Assert.notNull(e.getDataSourceType(), "数据源类型不能为空");
            if (e.getDataSourceType() == DataSourceType.IOT_SENSOR) {
                Assert.notBlank(e.getUniqueToken(), "设备传感器标识不能为空");
            }
        });
        RedisService redisService = SpringUtil.getBean(RedisConstant.MONITOR_REDIS_SERVICE, RedisService.class);
        //校验扩展配置
        this.configFieldValue = validExField(monitorType, exFields, redisService);
        //校验参数
        this.parameterList = validParamField(templateID, null, paramFields, redisService);
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }

    public void setDataSourceComposeType(Integer dataSourceComposeType) {
        this.dataSourceComposeType = dataSourceComposeType == null ?
                DataSourceComposeType.SINGLE_IOT : DataSourceComposeType.codeOf(dataSourceComposeType);
    }

    /**
     * 校验参数
     *
     * @param monitorType  监测类型
     * @param exFields     扩展配置列表
     * @param redisService redis服务
     * @return 参数列表
     */
    public static String validExField(Integer monitorType, List<SensorConfigField> exFields, RedisService redisService) {

        MonitorTypeCacheData monitorTypeCacheData = redisService.get(RedisKeys.MONITOR_TYPE_KEY,
                monitorType.toString(), MonitorTypeCacheData.class);
        if (CollUtil.isNotEmpty(monitorTypeCacheData.getMonitortypeFieldList())) {
            Assert.notEmpty(exFields, "扩展配置列表不能为空");
            Map<Integer, SensorConfigField> exMap = exFields.stream()
                    .filter(e -> StrUtil.isNotBlank(e.getValue()) && e.getId() != null)
                    .collect(Collectors.toMap(SensorConfigField::getId, e -> e));
            Map<String, Object> exConfig = monitorTypeCacheData.getMonitortypeFieldList().stream()
                    .filter(e -> FieldClass.EXTEND_CONFIG.equals(e.getFieldClass()))
                    .map(e -> {
                        JSONObject exVal = JSONUtil.parseObj(e.getExValues());
                        if (exVal.getBool("required", false)) {
                            Assert.isTrue(exMap.containsKey(e.getID()), "扩展配置{}不能为空", e.getFieldName());
                        }
                        return exMap.containsKey(e.getID()) ? Map.entry(e.getFieldToken(), exMap.get(e.getID()).getValue()) : null;
                    }).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return JSONUtil.toJsonStr(exConfig);
        }
        return null;
    }

    /**
     * 校验参数
     *
     * @param templateID   模板ID
     * @param subjectID    主体ID
     * @param paramFields  参数配置项
     * @param redisService redis服务
     * @return 参数列表
     */
    public static List<TbParameter> validParamField(Integer templateID, Integer subjectID, List<SensorConfigField> paramFields, RedisService redisService) {
        //校验参数
        if (CollUtil.isNotEmpty(paramFields)) {
            List<TbParameter> templateParams = redisService.getList(RedisKeys.PARAMETER_PREFIX_KEY +
                    ParameterSubjectType.TEMPLATE.getCode(), templateID.toString(), TbParameter.class);
            Map<Integer, SensorConfigField> paramFieldMap = paramFields.stream().collect(Collectors.toMap(SensorConfigField::getId, e -> e));
            return templateParams.stream().map(param -> {
                SensorConfigField field = paramFieldMap.get(param.getID());
                Assert.notNull(field, "参数配置项 [" + param.getID() + "]不存在");
                Assert.notNull(field.getValue(), "参数配置项 [" + param.getID() + "]值不能为空");
                TbParameter item = BeanUtil.copyProperties(param, TbParameter.class);
                item.setID(null);
                item.setSubjectID(subjectID);
                item.setSubjectType(ParameterSubjectType.SENSOR.getCode());
                item.setPaValue(paramFieldMap.get(param.getID()).getValue());
                return item;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}

    
    