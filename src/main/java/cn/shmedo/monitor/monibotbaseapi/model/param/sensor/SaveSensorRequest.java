package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.SensorConfigField;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

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
    private Integer dataSourceComposeType = 1;

    /**
     * 监测类型模板分布式唯一ID
     */
    private String templateDataSourceID;

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


    @Data
    public static class DataSource {

        /**
         * 数据源类型 1-物联网传感器 2-监测传感器
         */
        private Integer dataSourceType;

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
    }


    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    