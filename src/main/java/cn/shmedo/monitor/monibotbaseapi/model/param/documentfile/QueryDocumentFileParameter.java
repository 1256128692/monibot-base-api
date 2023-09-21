package cn.shmedo.monitor.monibotbaseapi.model.param.documentfile;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDocumentFileMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDocumentFile;
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
public class QueryDocumentFileParameter implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    @NotNull(message = "文件ID不能为空")
    private Integer fileID;

    @JsonIgnore
    private TbDocumentFile tbDocumentFile;

    @Override
    public ResultWrapper<?> validate() {
        TbDocumentFileMapper tbDocumentFileMapper = ContextHolder.getBean(TbDocumentFileMapper.class);
        tbDocumentFile = tbDocumentFileMapper.selectById(fileID);
        if(Objects.isNull(tbDocumentFile)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "为找到对应的资料文件");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
