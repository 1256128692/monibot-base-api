package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProjectImageParam implements ParameterValidator {
    private Integer projectID;

    @NotBlank
    private String fileName;
    @NotBlank
    private String imageContent;
    @NotBlank
    private String imageSuffix;


    @Override
    public ResultWrapper validate() {
        if (fileName.contains(".")){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "文件名称不能出现特殊字符,例如.");
        }
        return null;
    }
}
