package cn.shmedo.monitor.monibotbaseapi.model.param.weather;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDocumentFileMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDocumentFile;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;
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

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @JsonIgnore
    private TbProjectInfo tbProjectInfo;

    @Override
    public ResultWrapper<?> validate() {
        // 查询企业下项目，如果一个企业下存在多个项目，则默认选择第一个
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        List<TbProjectInfo> tbProjectInfoList = tbProjectInfoMapper.selectListByCompanyIDAndProjectIDList(companyID, null);
        if(CollectionUtil.isNullOrEmpty(tbProjectInfoList)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未查询到企业下对应的项目");
        }
        tbProjectInfo = tbProjectInfoList.get(0);
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
