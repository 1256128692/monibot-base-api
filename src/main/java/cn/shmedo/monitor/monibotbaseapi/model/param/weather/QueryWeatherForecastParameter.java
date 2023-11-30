package cn.shmedo.monitor.monibotbaseapi.model.param.weather;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDocumentFileMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDocumentFile;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

/**
 * @Author wuxl
 * @Date 2023/9/18 14:24
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.file
 * @ClassName: FileListParameter
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
public class QueryWeatherForecastParameter implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    @JsonIgnore
    private TbProjectInfo tbProjectInfo;

    @Override
    public ResultWrapper<?> validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        tbProjectInfo = tbProjectInfoMapper.selectById(projectID);
        if(Objects.isNull(tbProjectInfo)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未查询到对应的项目");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
