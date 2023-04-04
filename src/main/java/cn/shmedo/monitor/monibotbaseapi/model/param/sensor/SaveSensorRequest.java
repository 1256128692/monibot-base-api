package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataSourceComposeType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataSourceType;
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
    private DataSourceComposeType dataSourceComposeType = DataSourceComposeType.SINGLE_IOT;

    /**
     * 数据源列表
     */
    private List<DataSource> dataSourceList;

    /**
     * 扩展配置列表
     */
    private List<Field> exFields;

    /**
     * 参数列表
     */
    private List<Field> paramFields;

    public record Field(String ID, String value) {
    }

    public record DataSource(DataSourceType type, Integer ID) {
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

    
    