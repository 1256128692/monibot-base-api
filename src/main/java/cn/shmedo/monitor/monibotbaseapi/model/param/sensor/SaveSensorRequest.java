package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataSourceComposeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

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
     * 传感器名称
     */
    @Length(max = 15, message = "传感器名称不能超过15个字符")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9!@#¥%（）]+$", message = "传感器名称不合法")
    private String name;

    /**
     * 传感器别名
     */
    private String alias;

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 数据源
     */
    private DataSourceComposeType dataSource;

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

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    