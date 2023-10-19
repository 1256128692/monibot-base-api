package cn.shmedo.monitor.monibotbaseapi.model.param.documentfile;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbOtherDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbOtherDevice;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DocumentSubjectType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

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
public class QueryDocumentFilePageParameter implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "对象类型不能为空")
    private Integer subjectType;

    private Integer subjectID;

    private String fileName;

    private Boolean createTimeDesc;

    @Range(min = 1, max = 100, message = "分页大小必须在1-100之间")
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;

    @Range(min = 1, message = "当前页码必须大于0")
    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;

    @Override
    public ResultWrapper<?> validate() {
        if(DocumentSubjectType.PROJECT.getCode().equals(subjectType)){
            if(Objects.isNull(subjectID)){
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "对象类型为项目时，对象ID不能为空");
            }
            TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
            TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectById(subjectID);
            if(Objects.isNull(tbProjectInfo)){
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未找到对应项目");
            }
        }else {
            if(Objects.nonNull(subjectID)){
                TbOtherDeviceMapper tbOtherDeviceMapper = ContextHolder.getBean(TbOtherDeviceMapper.class);
                TbOtherDevice tbOtherDevice = tbOtherDeviceMapper.selectById(subjectID);
                if ((Objects.isNull(tbOtherDevice))){
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未找到对应其他设备");
                }
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        if(DocumentSubjectType.PROJECT.getCode().equals(subjectType) && Objects.nonNull(subjectID))
            return new Resource(subjectID.toString(), ResourceType.BASE_PROJECT);
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
